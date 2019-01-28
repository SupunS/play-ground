package test;

public class BazImpl {
    public int main(int left, int right) throws Exception {
        return bar(left, right);
    }

    public int bar(int left, int right) throws Exception {
        throw new Exception();
    }
}
