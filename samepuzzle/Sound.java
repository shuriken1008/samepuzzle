package samepuzzle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


//音声再生
public class Sound implements Runnable{
    private AudioInputStream stream;
    private byte[] buf;
    private AudioFormat format;

    private boolean isLoop;

    public Sound(String fileName) {
        loadWavFile(fileName);
    }

    public void loadWavFile(String fileName){
        try
        {
            File file=Paths.get("", fileName).toFile();
            if(file.exists())
            {
                // Read the sound file using AudioInputStream.
                stream=AudioSystem.getAudioInputStream(file);
                buf =new byte[stream.available()];
                stream.read(buf,0,buf.length);

                // Get an AudioFormat object from the stream.
                format=stream.getFormat();
            }else{
                System.out.println("none");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void playWav(){
        new Thread(
                () -> {
                    long nBytesRead=format.getFrameSize()*stream.getFrameLength();

                    // Construct a DataLine.Info object from the format.
                    DataLine.Info info=new DataLine.Info(SourceDataLine.class,format);
                    SourceDataLine line;
                    try {
                        line = (SourceDataLine)AudioSystem.getLine(info);
                        // Open and start the line.
                        line.open(format);
                        line.start();

                        // Write the data out to the line.
                        line.write(buf,0,(int)nBytesRead);

                        // Drain and close the line.
                        line.drain();
                        line.close();
                    } catch (LineUnavailableException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                })
                .start();


    }

    public long playWavLoop() throws InterruptedException{
        Thread t = new Thread(
                () -> {
                    while (isLoop){
                        long nBytesRead=format.getFrameSize()*stream.getFrameLength();

                        // Construct a DataLine.Info object from the format.
                        DataLine.Info info=new DataLine.Info(SourceDataLine.class,format);
                        SourceDataLine line;
                        try {
                            line = (SourceDataLine)AudioSystem.getLine(info);
                            // Open and start the line.
                            line.open(format);
                            line.start();

                            // Write the data out to the line.
                            line.write(buf,0,(int)nBytesRead);

                            // Drain and close the line.
                            line.drain();
                            line.close();
                        } catch (LineUnavailableException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            break;
                        }
                    }
                });
        
        t.start();
        return t.getId();
        
    }

    public static void main(String[] args){
        Sound s = new Sound("./samepuzzle/select.wav");

        s.playWav();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
}