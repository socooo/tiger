class Factorial { 
	public static void main(String[] a) {
        System.out.println(new Fac().ComputeFac(10));
    }
}
class Fac {
    public int ComputeFac(int num) {
        int num_aux;
        Add add;
        int addresult;
        
        if (num < 1)
            num_aux = 1;
        else
            num_aux = num * (this.ComputeFac(num-1));
        add = new Add();
        addresult = add.result();
        return addresult;
    }
}

class Add{
	int a;
	int b;
	int sum;
	
	
	public int result(){
		a = 1;
		b = 2;
		sum = a + b;
		return sum;
	}
}