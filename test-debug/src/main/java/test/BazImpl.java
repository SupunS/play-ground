package test;

public class BazImpl {
    public void foo(int left, int right) throws Exception {
        bar(left, right);
    }
    
    
    
    
    public void bar(int left, int right) throws Exception {
        throw new Exception();
    }
}
