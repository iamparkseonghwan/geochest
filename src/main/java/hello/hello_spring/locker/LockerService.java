package hello.hello_spring.locker;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class LockerService {

    private static final int LOCKER_COUNT = 60;      // 사물함 개수
    private static final long HOLD_MS = 30_000L;     // 30초 보유

    private final Map<Long, Locker> lockers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        reset();
    }

    // 전체 초기화
    public synchronized void reset() {
        lockers.clear();
        for (long i = 1; i <= LOCKER_COUNT; i++) {
            lockers.put(i, new Locker(i, LockerStatus.AVAILABLE, null, null));
        }
    }

    // 만료된 hold 정리
    private void cleanupExpiredHolds() {
        long now = System.currentTimeMillis();
        for (Locker l : lockers.values()) {
            if (l.getStatus() == LockerStatus.HELD &&
                    l.getHoldExpiresAt() != null &&
                    l.getHoldExpiresAt() <= now) {

                l.setStatus(LockerStatus.AVAILABLE);
                l.setOwner(null);
                l.setHoldExpiresAt(null);
            }
        }
    }

    // 전체 목록
    public synchronized List<Locker> findAll() {
        cleanupExpiredHolds();
        return lockers.values().stream()
                .sorted(Comparator.comparingLong(Locker::getId))
                .collect(Collectors.toList());
    }

    private Locker getLockerOrThrow(Long id) {
        Locker locker = lockers.get(id);
        if (locker == null) {
            throw new LockerException("존재하지 않는 사물함입니다: " + id);
        }
        return locker;
    }

    // 특정 사람이 이미 유효한 hold 갖고 있는지
    private Locker findActiveHoldByOwner(String owner) {
        if (owner == null || owner.isBlank()) return null;
        long now = System.currentTimeMillis();
        return lockers.values().stream()
                .filter(l -> l.getStatus() == LockerStatus.HELD &&
                        owner.equals(l.getOwner()) &&
                        l.getHoldExpiresAt() != null &&
                        l.getHoldExpiresAt() > now)
                .findFirst()
                .orElse(null);
    }

    // hold (보유)
    public synchronized Locker hold(Long id, String owner) {
        if (owner == null || owner.isBlank()) {
            throw new LockerException("이름을 입력해야 합니다.");
        }

        cleanupExpiredHolds();

        // 1인 1보유
        Locker existing = findActiveHoldByOwner(owner);
        if (existing != null) {
            throw new LockerException("이미 보유 중인 사물함이 있습니다. (ID: " + existing.getId() + ")");
        }

        Locker locker = getLockerOrThrow(id);

        if (locker.getStatus() == LockerStatus.SOLD) {
            throw new LockerException("이미 구매된 사물함입니다.");
        }
        if (locker.getStatus() == LockerStatus.HELD &&
                locker.getHoldExpiresAt() != null &&
                locker.getHoldExpiresAt() > System.currentTimeMillis() &&
                !owner.equals(locker.getOwner())) {
            throw new LockerException("이미 다른 사람이 보유 중입니다.");
        }

        locker.setStatus(LockerStatus.HELD);
        locker.setOwner(owner);
        locker.setHoldExpiresAt(System.currentTimeMillis() + HOLD_MS);

        return locker;
    }

    // buy (구매)
    public synchronized Locker buy(Long id, String owner) {
        if (owner == null || owner.isBlank()) {
            throw new LockerException("이름을 입력해야 합니다.");
        }

        cleanupExpiredHolds();

        Locker locker = getLockerOrThrow(id);

        if (locker.getStatus() != LockerStatus.HELD) {
            throw new LockerException("먼저 보유 상태가 되어야 구매할 수 있습니다.");
        }
        if (!owner.equals(locker.getOwner())) {
            throw new LockerException("본인 보유가 아니어서 구매할 수 없습니다.");
        }
        if (locker.getHoldExpiresAt() == null ||
                locker.getHoldExpiresAt() <= System.currentTimeMillis()) {
            throw new LockerException("보유 시간이 만료되었습니다.");
        }

        locker.setStatus(LockerStatus.SOLD);
        locker.setHoldExpiresAt(null);

        return locker;
    }
}