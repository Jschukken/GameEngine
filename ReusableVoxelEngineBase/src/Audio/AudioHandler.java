package Audio;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import org.lwjgl.util.vector.Vector3f;

/**
 * Handles audio
 * 
 * @author Jelle Schukken
 *
 */
public class AudioHandler {

	private List<IntBuffer> sources = new ArrayList<IntBuffer>();
	private List<IntBuffer> buffers = new ArrayList<IntBuffer>();

	/**
	 * creates audio session
	 */
	public AudioHandler() {
		boolean check = false;
		for(int i = 0; i<3 && !check;i++){
			check = true;
		try {
			AL.create();
		} catch (LWJGLException le) {
			le.printStackTrace();
			check = false;
		}
		}
	}

	/**
	 * loads a .wav file into the sound card and returns the ID
	 * @param file the file to load into the system
	 * @return the id of the sound file, needed to play the file
	 */
	public IntBuffer[] createSound(String file) {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);

		IntBuffer source = BufferUtils.createIntBuffer(1);

		AL10.alGenBuffers(buffer);
		buffers.add(buffer);
		WaveData waveFile = null;
		try {
			waveFile = WaveData.create(this.getClass().getClassLoader().getResource("res/" + file + ".wav"));
		} catch (Exception e) {
			System.out.println("Audio.AudioHandler: error encountered in creating waveFile for file: " + file);
			e.printStackTrace();
			System.exit(-1);
		}
		
		if (waveFile == null) {
			System.out.println("Audio.AudioHandler: waveFile is null");
			System.exit(-1);
		}
		AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();

		// Bind the buffer with the source.
		AL10.alGenSources(source);
		sources.add(source);
		AL10.alSourcei(source.get(0), AL10.AL_BUFFER, buffer.get(0));
		AL10.alSourcef(source.get(0), AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(source.get(0), AL10.AL_GAIN, 2.0f);

		IntBuffer[] temp = { source, buffer };
		return temp;
	}

	/**
	 * playes the audio file from a source position to a listener position
	 * @param audioID the audio to play
	 * @param sourceP the position of the source
	 * @param listenerP the position of the listener
	 */
	public void playAudio(IntBuffer[] audioID, Vector3f sourceP, Vector3f listenerP) {
		FloatBuffer sourcePos = (FloatBuffer) BufferUtils.createFloatBuffer(3)
				.put(new float[] { sourceP.x, sourceP.y, sourceP.z }).rewind();

		AL10.alSource(audioID[0].get(0), AL10.AL_POSITION, sourcePos);

		FloatBuffer listenerPos = (FloatBuffer) BufferUtils.createFloatBuffer(3)
				.put(new float[] { listenerP.x, listenerP.y, listenerP.z }).rewind();

		AL10.alListener(AL10.AL_POSITION, listenerPos);
		AL10.alSourcef(audioID[0].get(0), AL10.AL_GAIN, 2f);

		AL10.alSourcePlay(audioID[0].get(0));
	}

	/**
	 * plays a looping song until stopped
	 * @param audioID the song to play
	 */
	public void startSong(IntBuffer[] audioID) {
		AL10.alSourcei(audioID[0].get(0), AL10.AL_LOOPING, AL10.AL_TRUE);
		AL10.alSourcef(audioID[0].get(0), AL10.AL_GAIN, 0.05f);
		AL10.alSourcePlay(audioID[0].get(0));
	}

	/**
	 * stops a playing song
	 * @param audioID the song to stop
	 */
	public void endSong(IntBuffer[] audioID) {

		AL10.alSourceStop(audioID[0].get(0));
	}

	/**
	 * clears audio buffers and ends session
	 */
	public void cleanUp() {
		for (IntBuffer source : sources) {
			AL10.alDeleteSources(source);
		}
		for (IntBuffer buffer : buffers) {
			AL10.alDeleteBuffers(buffer);
		}
		AL.destroy();
	}
}
