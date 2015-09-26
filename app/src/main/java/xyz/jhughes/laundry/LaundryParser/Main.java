package xyz.jhughes.laundry.LaundryParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    ArrayList<Machine> machines;

    public Main(){
        getInformation(Constants.CARY_WEST);
    }

    public void getInformation(String location){
        machines = new ArrayList<Machine>();
        Document webpage = null;
        try{
            webpage = Jsoup.connect(location).get();
        } catch (IOException e){
            System.out.println("Webpage didn't Load");
        }
        Elements content = webpage.getElementsByTag("tr");
        for (Element e: content){
            if ( e.className().equals("") ){
                continue;
            }
            String name = e.getElementsByClass("name").get(0).text();
            String type = e.getElementsByClass("type").get(0).text();
            String status = e.getElementsByClass("status").get(0).text();
            String time = e.getElementsByClass("time").get(0).text();
            Machine m = new Machine(name, type, status, time);
            machines.add(m);
        }

        for ( int i=0; i<machines.size(); i++ ){
            Machine m = machines.get(i);
            System.out.println(m.getJSON());
        }

    }

    public static void main(String[] args) {
	    new Main();
    }

}
