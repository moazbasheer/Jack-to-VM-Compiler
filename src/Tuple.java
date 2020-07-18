public class Tuple {
    private String name;
    private String type;
    private int kind;
    private int index;
    public Tuple(String name,String type,int kind,int index){
        this.name = name;
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public int getKind() {
        return kind;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
