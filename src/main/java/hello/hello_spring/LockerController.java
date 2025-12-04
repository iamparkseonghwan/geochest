package hello.hello_spring;

import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/lockers")
public class LockerController {

    // 메모리 저장소: id -> Locker
    private final ConcurrentHashMap<Integer, Locker> store = new ConcurrentHashMap<>();

    // 서버 시작 시 사물함 초기화
    @PostConstruct
    public void init() {
        // 예시: 60개 사물함 생성
        for (int i = 1; i <= 60; i++) {
            Locker locker = new Locker();
            locker.setId(i);
            // label은 적당히 바꾸면 됨. "L1", "L2" 대신 "A1", "A2" 등으로도 가능
            locker.setLabel("L" + i);
            locker.setStatus(LockerStatus.AVAILABLE);
            locker.setOwner(null);
            locker.setHoldExpiresAt(null);

            store.put(i, locker);
        }
    }

    // 만료된 보유(HELD) 상태 정리
    private void cleanupExpiredHolds() {
        Instant now = Instant.now();
        for (Locker locker : store.values()) {
            if (locker.getStatus() == LockerStatus.HELD
                    && locker.getHoldExpiresAt() != null
                    && locker.getHoldExpiresAt().isBefore(now)) {
                locker.setStatus(LockerStatus.AVAILABLE);
                locker.setOwner(null);
                locker.setHoldExpiresAt(null);
            }
        }
    }

    // 전체 목록 조회
    @GetMapping
    public List<LockerDto> getAll() {
        cleanupExpiredHolds();

        List<LockerDto> result = new ArrayList<>();
        for (Locker locker : store.values()) {
            result.add(toDto(locker));
        }
        result.sort(Comparator.comparingInt(LockerDto::getId));
        return result;
    }

    // 특정 사물함 보유(hold)
    @PostMapping("/{id}/hold")
    public ResponseEntity<?> hold(@PathVariable int id,
                                  @RequestBody HoldRequest req) {
        cleanupExpiredHolds();

        Locker locker = store.get(id);
        if (locker == null) {
            return ResponseEntity.badRequest().body("없는 사물함입니다.");
        }

        // synchronized 블록 추가
        synchronized (locker) {
            if (locker.getStatus() == LockerStatus.SOLD) {
                return ResponseEntity.badRequest().body("이미 판매된 사물함입니다.");
            }

            if (locker.getStatus() == LockerStatus.HELD) {
                // 혹시 만료 시간이 이미 지났으면 AVAILABLE로 돌리고 다시 진행
                if (locker.getHoldExpiresAt() != null &&
                        locker.getHoldExpiresAt().isAfter(Instant.now())) {
                    return ResponseEntity.badRequest().body("이미 다른 사람이 선점 중입니다.");
                }
            }

            locker.setStatus(LockerStatus.HELD);
            locker.setOwner(req.getOwner());
            // 예시: 60초 동안만 보유
            locker.setHoldExpiresAt(Instant.now().plusSeconds(60));
        }

        return ResponseEntity.ok(toDto(locker));
    }

    // 구매 확정
    @PostMapping("/{id}/buy")
    public ResponseEntity<?> buy(@PathVariable int id,
                                 @RequestBody BuyRequest req) {
        cleanupExpiredHolds();

        Locker locker = store.get(id);
        if (locker == null) {
            return ResponseEntity.badRequest().body("없는 사물함입니다.");
        }

        // synchronized 블록 추가
        synchronized (locker) {
            if (locker.getStatus() != LockerStatus.HELD) {
                return ResponseEntity.badRequest().body("선점되지 않은 사물함입니다.");
            }

            // 본인이 hold한 좌석인지 확인
            if (locker.getOwner() == null || !locker.getOwner().equals(req.getOwner())) {
                return ResponseEntity.badRequest().body("이 사물함은 다른 사람이 선점했습니다.");
            }

            locker.setStatus(LockerStatus.SOLD);
            locker.setHoldExpiresAt(null);
        }

        return ResponseEntity.ok(toDto(locker));
    }

    // 관리자: 전체 초기화
    @PostMapping("/reset")
    public ResponseEntity<Void> reset() {
        for (Locker locker : store.values()) {
            locker.setStatus(LockerStatus.AVAILABLE);
            locker.setOwner(null);
            locker.setHoldExpiresAt(null);
        }
        return ResponseEntity.ok().build();
    }

    // 엔티티 -> DTO 변환
    private LockerDto toDto(Locker locker) {
        LockerDto dto = new LockerDto();
        dto.setId(locker.getId());
        dto.setLabel(locker.getLabel());
        dto.setStatus(locker.getStatus().name());
        dto.setOwner(locker.getOwner());
        dto.setHoldExpiresAt(
                locker.getHoldExpiresAt() != null
                        ? locker.getHoldExpiresAt().toEpochMilli()
                        : null
        );
        return dto;
    }
}
