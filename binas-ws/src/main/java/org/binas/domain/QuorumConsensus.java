package org.binas.domain;

import org.binas.station.ws.cli.StationClient;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class QuorumConsensus{
    private int nVotes;
    private int currentVotes;
    private boolean isFinished;

    public synchronized void addVote(){
        currentVotes++;
        if(currentVotes>=nVotes){
            isFinished = true;
        }
    }

    public boolean isFinished(){
        return isFinished;
    }

    QuorumConsensus(List<StationClient> scs, int nVotes){
        this.currentVotes = 0;
        this.nVotes = nVotes;
        for(StationClient sc: scs){
            quorumQuery(sc);
        }
    }

    abstract void quorumQuery(StationClient sc);
}

