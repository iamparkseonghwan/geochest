package hello.hello_spring.locker;

public class LockerRequest {

    private String owner;

    public LockerRequest() {
    }

    public LockerRequest(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}