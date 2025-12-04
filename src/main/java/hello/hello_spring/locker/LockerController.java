package hello.hello_spring.locker;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lockers")
public class LockerController {

    private final LockerService lockerService;

    public LockerController(LockerService lockerService) {
        this.lockerService = lockerService;
    }

    // 전체 조회
    @GetMapping
    public List<Locker> getAll() {
        return lockerService.findAll();
    }

    // 보유
    @PostMapping("/{id}/hold")
    public Locker hold(@PathVariable Long id, @RequestBody LockerRequest req) {
        return lockerService.hold(id, req.getOwner());
    }

    // 구매
    @PostMapping("/{id}/buy")
    public Locker buy(@PathVariable Long id, @RequestBody LockerRequest req) {
        return lockerService.buy(id, req.getOwner());
    }

    // 전체 초기화 (관리자)
    @PostMapping("/reset")
    public List<Locker> reset() {
        lockerService.reset();
        return lockerService.findAll();
    }
}