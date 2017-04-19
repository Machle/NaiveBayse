import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by tano on 18.12.16.
 */
public class Main {

    static String classification(Individual a, ArrayList<Individual> train_data){
        int classesNumber = 2;
        double[] classProb = new double[classesNumber];
        String[] classes = {"democrat", "republican"};
        double[] classSizes = {0, 0};

        for (int i = 0; i < train_data.size(); i++) {
            if(train_data.get(i).indiv_class.equals("democrat")){
                classSizes[0]++;
            } else {
                classSizes[1]++;
            }
        }
        classProb[0] = classSizes[0]/train_data.size();
        classProb[1] = classSizes[1]/train_data.size();

        double maxProb = 0;
        int maxProbClass = 0;

        for(int k = 0; k < classesNumber; k++){
            double P = classProb[k];
            for(int i = 0; i<a.attr.length;i++){
                double br=0;
                for(int j = 0; j<train_data.size();j++){
                    if(train_data.get(j).indiv_class.equals(classes[k])) {
                        if(train_data.get(j).attr[i].equals(a.attr[i])){
                            br++;
                        }
                    }
                }
                //System.out.println(br + " " + classSizes[k]);
                P=P*(br/classSizes[k]);
            }

            //System.out.println(P);

            if (P>maxProb) {
                maxProb = P;
                maxProbClass = k;
            }
        }

        return classes[maxProbClass];
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        InputStream fis = new FileInputStream("votes.data");
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);

        String line;
        ArrayList<Individual> indiv = new ArrayList<Individual>();
        while ((line = br.readLine()) != null) {
            String[] arr = line.split(",");
            Individual new_ind = new Individual();
            new_ind.indiv_class = arr[0];
            for(int i=1;i<arr.length;i++){
                if(arr[i].equals("n")){
                    new_ind.attr[i-1] = false;
                } else if(arr[i].equals("y")){
                    new_ind.attr[i-1] = true;
                } else {
                    new_ind.attr[i-1] = null;
                }
            }
            indiv.add(new_ind);
        }

        Collections.shuffle(indiv);
        ArrayList<ArrayList<Individual>> validation = new ArrayList<>();
        ArrayList<Individual> test = new ArrayList<>();
        int k = 0;
        int s = indiv.size()/10;
        for(int i = 0;i<10;i++){
            if(i == 9){
                for (int j = k; j < s; j++) {
                    test.add(indiv.get(j));
                }

            } else {
                validation.add(new ArrayList<>());
                for (int j = k; j < s; j++) {
                    validation.get(i).add(indiv.get(j));
                }
                k = s;
                s = s + indiv.size() / 10;
            }
        }

        //Test phase
        double[] count = new double[test.size()];
        double[] accuracy = new double[validation.size()];
        int t =0;
        while(t<accuracy.length) {
            for (int i = 0; i < test.size(); i++) {
                count[i] = 0;
                for (int j = 0; j < validation.size(); j++) {
                    String cl = classification(test.get(i), validation.get(j));
                    //System.out.println(cl + " " + test.get(i).indiv_class);
                    if (cl.equals(test.get(i).indiv_class)) {
                        count[i]++;
                    }
                }
            }
            double all = (double) validation.size(); //fixed
            double sum = 0;
            for(int i = 0; i<count.length;i++){
                sum+=count[i]/all;
            }
            accuracy[t] = sum/count.length;
            ArrayList<Individual> temp = test;
            test = validation.get(t);
            validation.set(t,temp);
            t++;
        }

        double sum_acc = 0;
        for(int i = 0;i<accuracy.length;i++){
            sum_acc+=accuracy[i];
            System.out.println("Test" + i + " accuracy: " + accuracy[i]);
        }
        System.out.println("Accuracy: " + sum_acc/accuracy.length);
    }
}
