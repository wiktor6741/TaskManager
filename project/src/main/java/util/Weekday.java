package util;

public enum Weekday {
    MON,
    TUE,
    WED,
    THU,
    FRI,
    SAT,
    SUN;

    public static Weekday parse(String string){
        return switch(string){
            case "MON" -> MON;
            case "TUE" -> TUE;
            case "WED" -> WED;
            case "THU" -> THU;
            case "FRI" -> FRI;
            case "SAT" -> SAT;
            case "SUN" -> SUN;
            default -> throw new IllegalArgumentException("Not a walid weekday");
        };
    }

    @Override
    public String toString(){
        return switch(this){
            case MON -> "MON";
            case TUE -> "TUE";
            case WED -> "WED";
            case THU -> "THU";
            case FRI -> "FRI";
            case SAT -> "SAT";
            case SUN -> "SUN";
        };
    }
}
