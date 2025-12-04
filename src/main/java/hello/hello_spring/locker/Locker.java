package hello.hello_spring.locker;

public class Locker {

    private Long id;
    private LockerStatus status;
    private String owner;         // 보유/구매자 이름
    private Long holdExpiresAt;   // epoch ms, null이면 없음

    public Locker() {
    }

    public Locker(Long id, LockerStatus status, String owner, Long holdExpiresAt) {
        this.id = id;
        this.status = status;
        this.owner = owner;
        this.holdExpiresAt = holdExpiresAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LockerStatus getStatus() {
        return status;
    }

    public void setStatus(LockerStatus status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getHoldExpiresAt() {
        return holdExpiresAt;
    }

    public void setHoldExpiresAt(Long holdExpiresAt) {
        this.holdExpiresAt = holdExpiresAt;
    }
}