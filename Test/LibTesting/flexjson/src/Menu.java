import java.util.List;

/**
 * Created by isurur on 11/24/16.
 */
public class Menu {

    private String name;

    private int price;

    private List<Meta> meta ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<Meta> getMeta() {
        return meta;
    }

    public void setMeta(List<Meta> meta) {
        this.meta = meta;
    }
}
