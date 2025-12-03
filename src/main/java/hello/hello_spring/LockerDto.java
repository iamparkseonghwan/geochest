package hello.hello_spring;

public class LockerDto {

    private int id;
    private String label;
    private String status;      // "AVAILABLE", "HELD", "SOLD"
    private String owner;
    private Long holdExpiresAt; // epoch millis, null 가능

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
