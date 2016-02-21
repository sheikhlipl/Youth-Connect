package com.lipl.youthconnect.youth_connect.demo;

import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.Database;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Android Luminous on 2/21/2016.
 */
public class Synchronize {

    public Replication pullReplication;
    public Replication pushReplication;

    public static class Builder {
        public Replication pullReplication;
        public Replication pushReplication;

        public Builder(Database database, String url, Boolean continuousPull) {

            if (pullReplication == null && pushReplication == null) {

                URL syncUrl;
                try {
                    syncUrl = new URL(url);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }

                pullReplication = database.createPullReplication(syncUrl);
                if (continuousPull == true)
                    pullReplication.setContinuous(true);

                pushReplication = database.createPushReplication(syncUrl);
                pushReplication.setContinuous(true);
            }
        }

        public Builder addChangeListener(Replication.ChangeListener changeListener) {
            pullReplication.addChangeListener(changeListener);
            pushReplication.addChangeListener(changeListener);

            return this;
        }

        public Synchronize build() {
            return new Synchronize(this);
        }

    }

    private Synchronize(Builder builder) {
        pullReplication = builder.pullReplication;
        pushReplication = builder.pushReplication;
    }

    public void start() {
        pullReplication.start();
        pushReplication.start();
    }

    public void destroyReplications() {
        pullReplication.stop();
        pushReplication.stop();
        pullReplication.deleteCookie("SyncGatewaySession");
        pushReplication.deleteCookie("SyncGatewaySession");
        pullReplication = null;
        pushReplication = null;
    }
}
