import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.Map;

public class MockDevices extends NanoHTTPD {/**This is the code used to simulate the smart devices to be called by Main */
    private boolean lightState = false;
    private int fanState = 0;
    private int AC = 0;
    public MockDevices(int port) throws IOException{
        super(port);
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("Mock Smart Device running at http://localhost:"+port);
    }

    @Override
    public Response serve(IHTTPSession session){
        String uri = session.getUri();
        Map<String,String> params = session.getParms();
        System.out.println("received request: " + uri);
        switch(uri){
            case "/flip":
                if(lightState){
                    lightState=false;
                    return newFixedLengthResponse("Light turned OFF");
                }
                else{
                    lightState=true;
                    return newFixedLengthResponse("Light turned ON");
                }
                
            case "/setFan":
                if(params.containsKey("value")){
                    try{
                        int value=Integer.parseInt(params.get("value"));
                        if(value<0||value>2){
                            throw new NumberFormatException();
                        }
                        fanState=value;
                        return newFixedLengthResponse("Fan set to " + fanState);
                    
                    }catch(NumberFormatException e){
                        return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid Fan value");
                    }
                }
                else{
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "400 Bad Request");
                }
            case "/setAC":
                if(params.containsKey("value")){
                    try{
                        AC = Integer.parseInt(params.get("value"));
                        if(AC==0)
                            return newFixedLengthResponse("Air Conditioner turned off");
                        return newFixedLengthResponse("Air Conditioner set to"+AC);
                    }catch(NumberFormatException e){
                        return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid AC value");
                    }
                }
                else{
                    return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid AC value");
                }
            case "/lightStatus":
                if(lightState)
                    return newFixedLengthResponse("Light is ON");
                else
                    return newFixedLengthResponse("Light is OFF");
            case "/fanStatus":
                return newFixedLengthResponse("Fan currently set to"+fanState);
            case "/ACStatus":
                if(AC==0){
                    return newFixedLengthResponse("Air Conditioner is turned off");
                }
                else{
                    return newFixedLengthResponse("Air Conditioner is set to "+AC);
                }
            default:
                return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 Not Found");
        }
    }


    public static void main(String[] args){
        try{
            new MockDevices(8080);
        }catch (IOException e){
            System.err.println("Failed to start mock server: " + e.getMessage());
        }
    }
}