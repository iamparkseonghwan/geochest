package hello.hello_spring;

import java.time.Instant;

public class Locker {

    private int id;                 // 1, 2, 3 ...
    private String label;           // 화면에 보여줄 이름 (예: L1, L2 ...)
    private LockerStatus status;    // AVAILABLE / HELD / SOLD
    private String owner;           // 보유/구매한 사람 이름
    private Instant holdExpiresAt;  // 보유 만료 시각 (null이면 없음)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public Instant getHoldExpiresAt() {
        return holdExpiresAt;
    }

    public void setHoldExpiresAt(Instant holdExpiresAt) {
        this.holdExpiresAt = holdExpiresAt;
    }
}
