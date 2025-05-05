import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Scanner;
public class app {
    private static volatile boolean keepChecking=true;
    public static class controller{

        public controller(){

        }
        public boolean update(){
            try{
                if(this.getLightStatus().equals("Light is ON")){
                    this.light();
                }
                this.fan(0);
                this.AC(0);
                return true;
            }catch(Exception e){
                System.err.println("Error resetting");
            }
            return false;
        }

        public String light(){
            return this.sendGETCommand("/flip");
        }
        public String fan(int value){
            return this.sendPOSTCommand("/setFan?value="+value, "");
        }
        public String AC(int value){
            return this.sendPOSTCommand("/setAC?value="+value, "");
        }
        public String getLightStatus(){
            return this.sendGETCommand("/lightStatus");
        }
        public String getFanStatus(){
            return this.sendGETCommand("/fanStatus");
        }
        public String getACStatus(){
            return this.sendGETCommand("/ACStatus");
        }
        private String sendGETCommand(String Command){
            try {
                URL url = new URL("http://localhost:8080"+Command);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(5000);
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while((line = in.readLine())!=null){
                    response.append(line);
                }
                in.close();
                return response.toString();
            }catch (Exception e){
                System.err.println("error connecting to device: "+e.getMessage());
                return "false";
            }
        }
        
        private String sendPOSTCommand(String Command, String data) {
            try {
                URL url = new URL("http://localhost:8080" + Command);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setConnectTimeout(5000);
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = data.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
        
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString();
            } catch (Exception e) {
                System.err.println("Error sending POST: " + e.getMessage());
                return "false";
            }
        }
        
    
    }
    public static void updater(controller c){
        Thread checkerThread = new Thread(new Runnable(){
            @Override
            public void run(){
                while(keepChecking){
                    LocalDateTime now = LocalDateTime.now();
                    if (now.getMonthValue() == 1 && now.getDayOfMonth() == 1 && now.getHour() == 1 && now.getMinute() == 0){
                        System.out.println("January 1st reached turnning off all devices");
                        c.update();
                        //Add something here
                    }
                    try{
                        Thread.sleep(60000);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
                System.out.println("Closing the thread");//So I know to close the thread
            }
        });
        checkerThread.start();
    }
    
    
    public static void main(String[] args){
        boolean run=true;
        controller c = new controller();
        Scanner scanner = new Scanner(System.in);//User input will simulate the signals sent from the lightswitch fan setting and thermostat
        updater(c);
        while(run){
            System.out.println("input the action:");
            System.out.println("1 to flip the light switch");
            System.out.println("2 to set the speed of the fan");
            System.out.println("3 change the temperature of the thermostat");
            System.out.println("4 close the application");
            int line = scanner.nextInt();
            scanner.nextLine();
            switch(line){
                case 1:
                    c.light();
                    System.out.println(c.getLightStatus());
                    break;
                case 2:
                    System.out.println("What setting is the fan being set to?");
                    line = scanner.nextInt();
                    c.fan(line);
                    System.out.println(c.getFanStatus());
                    break;
                case 3:
                    System.out.println("What setting is the AC it being set to?");
                    String here = scanner.nextLine();
                    if(here.equalsIgnoreCase("OFF")){
                        c.AC(0);
                    }
                    else{
                        c.AC(Integer.parseInt(here));
                    }
                    System.out.println(c.getACStatus());
                    break;
                case 4:
                    System.out.println("closing the application");
                    run=false;
                    break;
            }
        }
        keepChecking=false;
        scanner.close();
    }

}
