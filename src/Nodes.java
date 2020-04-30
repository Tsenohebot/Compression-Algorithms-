public class Nodes implements Comparable<Nodes> {

    Nodes right, left, root;
    Integer cand;
    Integer freq;

    public Nodes (Integer cand, Integer freq){
        this.cand = cand;
        this.freq = freq;
    }

    public Nodes (Integer freq){
        this.freq = freq;
    }


    @Override
    public int compareTo(Nodes o) {
        if(freq > o.freq) return 1;
        else if(freq < o.freq) return -1;
        else return 0;
    }
}
