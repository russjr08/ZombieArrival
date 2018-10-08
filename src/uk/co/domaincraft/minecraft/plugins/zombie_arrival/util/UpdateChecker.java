package uk.co.domaincraft.minecraft.plugins.zombie_arrival.util;


import com.kronosad.api.internet.ReadURL;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ReleaseType;

public class UpdateChecker {

    private ReadURL readURL;
    private double localVersion;
    private ReleaseType releaseType;
    private double serverVersion = 0.0;

    public UpdateChecker(double localVersion, ReleaseType releaseType) {
        this.localVersion = localVersion;
        this.releaseType = releaseType;
    }


    public boolean needsUpdate() throws Exception {

        if(this.releaseType == ReleaseType.ALPHA){
            readURL = new ReadURL("http://api.kronosad.com/minecraft/ZombieArrival/Alpha/update.txt");
        }else if(this.releaseType == ReleaseType.BETA){
            readURL = new ReadURL("http://api.kronosad.com/minecraft/ZombieArrival/Beta/update.txt");
        }else if(this.releaseType == ReleaseType.RELEASE){
            readURL = new ReadURL("http://api.kronosad.com/minecraft/ZombieArrival/Release/update.txt");
        }

        String serverString = readURL.read();


        if(serverString != null){
            serverVersion = Double.parseDouble(serverString);
        }

        return serverVersion > this.localVersion;


    }

    public double getServerVersion() {
        return serverVersion;
    }

    public double getLocalVersion() {
        return localVersion;
    }

    public ReleaseType getReleaseType() {
        return releaseType;
    }
}
