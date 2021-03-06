// Copyright (c) 2013, Peter James
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//     * Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above copyright
//       notice, this list of conditions and the following disclaimer in the
//       documentation and/or other materials provided with the distribution.
//     * Neither the name of the <organization> nor the
//       names of its contributors may be used to endorse or promote products
//       derived from this software without specific prior written permission.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

import java.io.*;
import java.util.*;
import java.text.*;

public abstract class SyncPacket
{
    public int index;
    public String timestamp;
    public boolean forcedSync;

    public byte[] bytes;
    
    public int packetType;

    public int sequenceNum;

    public boolean isRightHand;
    public boolean isFlowerEnabled;
    public boolean isDistanceEnabled;
    public boolean isCaloriesEnabled;
    public boolean isFloorsEnabled;
    public boolean isClockEnabled;
    public boolean isGreetingEnabled;
    public boolean isChatterEnabled;
    public boolean isUsing24HourClock;
    public boolean isMetricDistance;

    public int walkingStrideLengthInMillimeters;
    public int runningStrideLengthInMillimeters;
    public int bmr;

    public int timezone;

    public String greeting;
    public String[] chatter = new String[3];

    public Date lastSyncTime;

    public TreeMap<Date, List<Integer>> stepsCollection;
    public TreeMap<Date, List<Integer>> elevationsCollection;

    public boolean isAlarmDataPresent;
    public int numAlarms;
    public AlarmEntry[] alarms;

    public abstract void parse();

    private static SimpleDateFormat fullDateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private static SimpleDateFormat timeFormatter = new SimpleDateFormat("           HH:mm:ss");

    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("Packet"); builder.append('\n');
        builder.append("    index: " + this.index); builder.append('\n');
        builder.append("    timestamp: " + this.timestamp); builder.append('\n');
        builder.append("    type: " + this.getPacketTypeString()); builder.append('\n');
        builder.append("    size: " + this.bytes.length + " bytes"); builder.append('\n');
        builder.append("    forcedSync: " + this.forcedSync); builder.append('\n');
        builder.append("    sequenceNum: " + this.sequenceNum); builder.append('\n');
        builder.append("    isRightHand: " + this.isRightHand); builder.append('\n');
        builder.append("    isFlowerEnabled: " + this.isFlowerEnabled); builder.append('\n');
        builder.append("    isDistanceEnabled: " + this.isDistanceEnabled); builder.append('\n');
        builder.append("    isCaloriesEnabled: " + this.isCaloriesEnabled); builder.append('\n');
        builder.append("    isFloorsEnabled: " + this.isFloorsEnabled); builder.append('\n');
        builder.append("    isClockEnabled: " + this.isClockEnabled); builder.append('\n');
        builder.append("    isGreetingEnabled: " + this.isGreetingEnabled); builder.append('\n');
        builder.append("    isChatterEnabled: " + this.isChatterEnabled); builder.append('\n');
        builder.append("    isUsing24HourClock: " + this.isUsing24HourClock); builder.append('\n');
        builder.append("    isAlarmDataPresent: " + this.isAlarmDataPresent); builder.append('\n');
        builder.append("    isMetricDistance: " + this.isMetricDistance); builder.append('\n');
        builder.append("    timezone: " + this.timezone + " (?)"); builder.append('\n');
        builder.append("    walkingStrideLength: " + this.walkingStrideLengthInMillimeters + " mm"); builder.append('\n');
        builder.append("    runningStrideLength: " + this.runningStrideLengthInMillimeters + " mm"); builder.append('\n');
        builder.append("    bmr: " + this.bmr + " (?)"); builder.append('\n');
        builder.append("    greeting: " + this.greeting); builder.append('\n');
        builder.append("    chatter[0]: " + this.chatter[0]); builder.append('\n');
        builder.append("    chatter[1]: " + this.chatter[1]); builder.append('\n');
        builder.append("    chatter[2]: " + this.chatter[2]); builder.append('\n');
        builder.append("    lastSyncTime: " + this.lastSyncTime); builder.append('\n');
        builder.append("    numAlarms: " + this.numAlarms); builder.append('\n');

        for (int i = 0; i < this.numAlarms; i++)
        {
            builder.append("        alarm:  " + this.alarms[i].toString()); builder.append('\n');
        }

        builder.append("    steps (per minute): "); builder.append('\n');
        if (this.stepsCollection != null)
        {
            for (Map.Entry<Date, List<Integer>> entry : this.stepsCollection.entrySet())
            {
                Date date = entry.getKey();
                List<Integer> stepsList = entry.getValue();

                String prefix = "        " + fullDateFormatter.format(date) + ": ";
                builder.append(prefix);

                int prefixLen = prefix.length();
                boolean first = true;
                for (int steps : stepsList)
                {
                    // Only use the blank padding on the second and subsequent lines
                    if (!first)
                    {
                        // TODO: do this the right way (String.format()?)
                        for (int i = 0; i < prefixLen; i++) { builder.append(' '); }
                    }

                    first = false;
                    builder.append(steps); builder.append('\n');
                }
            }
        }

        builder.append("    elevation (per minute): "); builder.append('\n');
        if (this.elevationsCollection != null)
        {
            for (Map.Entry<Date, List<Integer>> entry : this.elevationsCollection.entrySet())
            {
                Date date = entry.getKey();
                List<Integer> elevationList = entry.getValue();

                String prefix = "        " + fullDateFormatter.format(date) + ": ";
                builder.append(prefix);

                int prefixLen = prefix.length();
                boolean first = true;
                for (int elevation : elevationList)
                {
                    // Only use the blank padding on the second and subsequent lines
                    if (!first)
                    {
                        // TODO: do this the right way (String.format()?)
                        for (int i = 0; i < prefixLen; i++) { builder.append(' '); }
                    }

                    first = false;
                    builder.append((elevation / 10) + " floors (" + elevation + " ft)"); builder.append('\n');
                }
            }
        }

        return builder.toString();
    }

    public String getPacketTypeString()
    {
        switch (this.packetType)
        {
            case SyncPacketType.MegaDump:
                return "MegaDump";

            case SyncPacketType.MicroDump:
                return "MicroDump";

            case SyncPacketType.ServerResponse:
                return "ServerResponse";

            default:
                return "Unknown: " + this.packetType;
        }
    }

    protected void parsePersonalMetrics(int startIndex)
    {
        this.walkingStrideLengthInMillimeters = BinaryUtils.composeInt((byte)0, (byte)0, this.bytes[startIndex + 1], this.bytes[startIndex]);
        this.runningStrideLengthInMillimeters = BinaryUtils.composeInt((byte)0, (byte)0, this.bytes[startIndex + 3], this.bytes[startIndex + 2]);

        // TODO: What is this value? It's influenced by weight, height, age...
        this.bmr = BinaryUtils.composeInt((byte)0, (byte)0, this.bytes[startIndex + 5], this.bytes[startIndex + 4]);
    }

    protected void parseDisplayConfiguration(int startIndex)
    {
        int flags = 0;

        //this.timezone = BinaryUtils.toInt(this.bytes[startIndex]);

        flags = BinaryUtils.toInt(this.bytes[startIndex]);
        this.isMetricDistance = !BinaryUtils.isBitSet(flags, 0); 

        flags = BinaryUtils.toInt(this.bytes[startIndex + 1]);
        this.isGreetingEnabled = BinaryUtils.isBitSet(flags, 5); 
        this.isUsing24HourClock = BinaryUtils.isBitSet(flags, 3); 
        this.isRightHand = BinaryUtils.isBitSet(flags, 2); 

        flags = BinaryUtils.toInt(this.bytes[startIndex + 9]);
        this.isChatterEnabled = BinaryUtils.isBitSet(flags, 7); 
        this.isFlowerEnabled = BinaryUtils.isBitSet(flags, 6); 
        this.isFloorsEnabled = BinaryUtils.isBitSet(flags, 4); 
        this.isClockEnabled = BinaryUtils.isBitSet(flags, 3); 
        this.isCaloriesEnabled = BinaryUtils.isBitSet(flags, 2); 
        this.isDistanceEnabled = BinaryUtils.isBitSet(flags, 1); 
    }

    protected void parseGreetingAndChatter(int startIndex)
    {
        try
        {
            this.greeting = new String(this.bytes, startIndex, 10, "UTF8");
            this.chatter[0] = new String(this.bytes, startIndex + 10, 10, "UTF8");
            this.chatter[1] = new String(this.bytes, startIndex + 20, 10, "UTF8");
            this.chatter[2] = new String(this.bytes, startIndex + 30, 10, "UTF8");
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
        }
    }

    protected final Date parseDateBigEndian(int startIndex)
    {
        return this.parseDate(startIndex, true);
    }

    protected final Date parseDateLittleEndian(int startIndex)
    {
        return this.parseDate(startIndex, false);
    }

    private Date parseDate(int startIndex, boolean flipBytes)
    {
        int epoch = 0;

        if (flipBytes)
        {
            epoch = BinaryUtils.composeInt(
                this.bytes[startIndex + 3],
                this.bytes[startIndex + 2],
                this.bytes[startIndex + 1],
                this.bytes[startIndex]);
        }
        else
        {
            epoch = BinaryUtils.composeInt(
                this.bytes[startIndex],
                this.bytes[startIndex + 1],
                this.bytes[startIndex + 2],
                this.bytes[startIndex + 3]);
        }

        return new java.util.Date(epoch*1000L);
    }

    public void dump()
    {
        System.out.println(" = = = = = Packet " + this.index + " = = = = ");

        this.dumpHex();
        
        System.out.println(this.toString());
    }

    public void dumpHex()
    {
        StringBuilder hexLineBuilder = new StringBuilder();
        StringBuilder asciiLineBuilder = new StringBuilder();

        int maxLineByteCount = 24;
        int lineCount = 0;
        int lineByteCount = 0;

        //System.out.println("dumpHex: bytes: " + this.bytes.length);
        for (byte b : this.bytes)
        {
            hexLineBuilder.append(String.format("%02X", b));
            //hexLineBuilder.append(" ");

            char c = '.';
            if (b >= 32 && b <= 126)
            {
                c = (char)b;
            }

            asciiLineBuilder.append(c);

            lineByteCount++;

            if (lineByteCount == maxLineByteCount)
            {
                this.dumpHexLine(lineCount, hexLineBuilder, asciiLineBuilder, maxLineByteCount);

                // Clear the buffers for the next line
                hexLineBuilder.setLength(0);
                asciiLineBuilder.setLength(0);
                lineByteCount = 0;

                lineCount++;
            }
        }

        // Only dump the last line if there's something here
        if (hexLineBuilder.length() > 0)
        {
            this.dumpHexLine(lineCount, hexLineBuilder, asciiLineBuilder, maxLineByteCount);
        }
    }

    public void dumpHexLine(int currentLineNumber, StringBuilder hexLineBuilder, StringBuilder asciiLineBuilder, int maxLineByteCount)
    {
        // If we get a partial line, we still want to line up the sections
        while (asciiLineBuilder.length() < maxLineByteCount)
        {
            hexLineBuilder.append("  ");
            //hexLineBuilder.append(" ");

            asciiLineBuilder.append(" ");
        }

        // Dump out the line segments
        //System.out.print(String.format("%08X: ", (currentLineNumber * maxLineByteCount)));
        System.out.print(hexLineBuilder.toString());
        //System.out.print("  ");
        //System.out.print(asciiLineBuilder.toString());
        System.out.println();
    }
}
