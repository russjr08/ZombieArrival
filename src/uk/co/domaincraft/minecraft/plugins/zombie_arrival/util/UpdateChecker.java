package uk.co.domaincraft.minecraft.plugins.zombie_arrival.util;


import com.kronosad.api.internet.ReadURL;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ReleaseType;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ZombieArrival;

public class UpdateChecker {

    private static ReadURL readURL;
    public boolean needsUpdate = false;
    private static String serverString;
    public double serverVer;


    public void checkForUpdate() throws Exception{
        if(ZombieArrival.releaseType == ReleaseType.ALPHA){
            readURL = new ReadURL("http://api.kronosad.com/minecraft/ZombieArrival/Alpha/update.txt");
        }else if(ZombieArrival.releaseType == ReleaseType.BETA){
            readURL = new ReadURL("http://api.kronosad.com/minecraft/ZombieArrival/Beta/update.txt");
        }else if(ZombieArrival.releaseType == ReleaseType.RELEASE){
            readURL = new ReadURL("http://api.kronosad.com/minecraft/ZombieArrival/Release/update.txt");
        }

        serverString = readURL.read();


        if(serverString != null){
            serverVer = Double.parseDouble(serverString);
        }

        if(serverVer > ZombieArrival.version){
            needsUpdate = true;
        }


    }


}
