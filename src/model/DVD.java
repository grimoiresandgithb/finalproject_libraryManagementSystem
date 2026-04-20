package model;

public class DVD extends Item {

    private String director;
    private int runtimeMinutes;

    public DVD() {
        super();
    }

    public DVD(int id, String title, boolean available,
               String director, int runtimeMinutes) {
        super(id, title, available);
        this.director = director;
        this.runtimeMinutes = runtimeMinutes;
    }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public int getRuntimeMinutes() { return runtimeMinutes; }
    public void setRuntimeMinutes(int runtimeMinutes) { this.runtimeMinutes = runtimeMinutes; }

    @Override
    public String getItemType() {
        return "dvd";
    }

    @Override
    public String toString() {
        return String.format("[%d] %s directed by %s (%d min) - %s",
                id, title, director, runtimeMinutes,
                available ? "Available" : "On Loan");
    }
}
