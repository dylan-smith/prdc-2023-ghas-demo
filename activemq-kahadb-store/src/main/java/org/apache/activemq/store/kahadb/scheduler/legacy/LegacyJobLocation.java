/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.store.kahadb.scheduler.legacy;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.activemq.store.kahadb.disk.journal.Location;
import org.apache.activemq.store.kahadb.disk.util.VariableMarshaller;

final class LegacyJobLocation {

    private String jobId;
    private int repeat;
    private long startTime;
    private long delay;
    private long nextTime;
    private long period;
    private String cronEntry;
    private final Location location;

    public LegacyJobLocation(Location location) {
        this.location = location;
    }

    public LegacyJobLocation() {
        this(new Location());
    }

    public void readExternal(DataInput in) throws IOException {
        this.jobId = in.readUTF();
        this.repeat = in.readInt();
        this.startTime = in.readLong();
        this.delay = in.readLong();
        this.nextTime = in.readLong();
        this.period = in.readLong();
        this.cronEntry = in.readUTF();
        this.location.readExternal(in);
    }

    public void writeExternal(DataOutput out) throws IOException {
        out.writeUTF(this.jobId);
        out.writeInt(this.repeat);
        out.writeLong(this.startTime);
        out.writeLong(this.delay);
        out.writeLong(this.nextTime);
        out.writeLong(this.period);
        if (this.cronEntry == null) {
            this.cronEntry = "";
        }
        out.writeUTF(this.cronEntry);
        this.location.writeExternal(out);
    }

    /**
     * @return the jobId
     */
    public String getJobId() {
        return this.jobId;
    }

    /**
     * @param jobId
     *            the jobId to set
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the repeat
     */
    public int getRepeat() {
        return this.repeat;
    }

    /**
     * @param repeat
     *            the repeat to set
     */
    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    /**
     * @return the start
     */
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * @param start
     *            the start to set
     */
    public void setStartTime(long start) {
        this.startTime = start;
    }

    /**
     * @return the nextTime
     */
    public synchronized long getNextTime() {
        return this.nextTime;
    }

    /**
     * @param nextTime
     *            the nextTime to set
     */
    public synchronized void setNextTime(long nextTime) {
        this.nextTime = nextTime;
    }

    /**
     * @return the period
     */
    public long getPeriod() {
        return this.period;
    }

    /**
     * @param period
     *            the period to set
     */
    public void setPeriod(long period) {
        this.period = period;
    }

    /**
     * @return the cronEntry
     */
    public synchronized String getCronEntry() {
        return this.cronEntry;
    }

    /**
     * @param cronEntry
     *            the cronEntry to set
     */
    public synchronized void setCronEntry(String cronEntry) {
        this.cronEntry = cronEntry;
    }

    /**
     * @return if this JobLocation represents a cron entry.
     */
    public boolean isCron() {
        return getCronEntry() != null && getCronEntry().length() > 0;
    }

    /**
     * @return the delay
     */
    public long getDelay() {
        return this.delay;
    }

    /**
     * @param delay
     *            the delay to set
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return "Job [id=" + jobId + ", startTime=" + new Date(startTime) +
               ", delay=" + delay + ", period=" + period +
               ", repeat=" + repeat + ", nextTime=" + new Date(nextTime) + "]";
    }

    static class JobLocationMarshaller extends VariableMarshaller<List<LegacyJobLocation>> {
        static final JobLocationMarshaller INSTANCE = new JobLocationMarshaller();

        @Override
        public List<LegacyJobLocation> readPayload(DataInput dataIn) throws IOException {
            List<LegacyJobLocation> result = new ArrayList<LegacyJobLocation>();
            int size = dataIn.readInt();
            for (int i = 0; i < size; i++) {
                LegacyJobLocation jobLocation = new LegacyJobLocation();
                jobLocation.readExternal(dataIn);
                result.add(jobLocation);
            }
            return result;
        }

        @Override
        public void writePayload(List<LegacyJobLocation> value, DataOutput dataOut) throws IOException {
            dataOut.writeInt(value.size());
            for (LegacyJobLocation jobLocation : value) {
                jobLocation.writeExternal(dataOut);
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cronEntry == null) ? 0 : cronEntry.hashCode());
        result = prime * result + (int) (delay ^ (delay >>> 32));
        result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + (int) (nextTime ^ (nextTime >>> 32));
        result = prime * result + (int) (period ^ (period >>> 32));
        result = prime * result + repeat;
        result = prime * result + (int) (startTime ^ (startTime >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        LegacyJobLocation other = (LegacyJobLocation) obj;

        if (cronEntry == null) {
            if (other.cronEntry != null) {
                return false;
            }
        } else if (!cronEntry.equals(other.cronEntry)) {
            return false;
        }

        if (delay != other.delay) {
            return false;
        }

        if (jobId == null) {
            if (other.jobId != null)
                return false;
        } else if (!jobId.equals(other.jobId)) {
            return false;
        }

        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }

        if (nextTime != other.nextTime) {
            return false;
        }
        if (period != other.period) {
            return false;
        }
        if (repeat != other.repeat) {
            return false;
        }
        if (startTime != other.startTime) {
            return false;
        }

        return true;
    }
}
