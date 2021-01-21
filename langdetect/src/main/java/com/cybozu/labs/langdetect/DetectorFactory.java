package com.cybozu.labs.langdetect;

import com.cybozu.labs.langdetect.util.LangProfile;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Language Detector Factory Class
 * 
 * This class manages an initialization and constructions of {@link Detector}.
 *
 * Note: This class replicates the behaviour of the old DetectorFactory (now DetectorFactorySingleton)
 * but without the use of a globally modifiable singleton to configure Detectors. Previous implementation
 * did not play nice if you ever want more than one language detector with different configurations
 * in the same JVM...
 *
 * <ul>
 * <li>4x faster improvement based on Elmer Garduno's code. Thanks!</li>
 * </ul>
 * 
 * @see Detector
 * @author Nakatani Shuyo
 */
public class DetectorFactory {
    public HashMap<String, double[]> wordLangProbMap;
    public ArrayList<String> langlist;
    public Long seed = null;

    public DetectorFactory() {
        wordLangProbMap = new HashMap<>();
        langlist = new ArrayList<>();
    }

    /**
     * Load profiles from specified directory.
     * This method must be called once before language detection.
     *  
     * @param profileDirectory profile directory path
     * @throws LangDetectException  Can't open profiles(error code = {@link ErrorCode#FileLoadError})
     *                              or profile's format is wrong (error code = {@link ErrorCode#FormatError})
     */
    public void loadProfile(String profileDirectory) throws LangDetectException {
        loadProfile(new File(profileDirectory));
    }

    /**
     * Load profiles from specified directory.
     * This method must be called once before language detection.
     *  
     * @param profileDirectory profile directory path
     * @throws LangDetectException  Can't open profiles(error code = {@link ErrorCode#FileLoadError})
     *                              or profile's format is wrong (error code = {@link ErrorCode#FormatError})
     */
    public void loadProfile(File profileDirectory) throws LangDetectException {
        File[] listFiles = profileDirectory.listFiles();
        if (listFiles == null)
            throw new LangDetectException(ErrorCode.NeedLoadProfileError, "Not found profile: " + profileDirectory);
            
        int langsize = listFiles.length, index = 0;
        for (File file: listFiles) {
            if (file.getName().startsWith(".") || !file.isFile()) continue;
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                LangProfile profile = JSON.decode(is, LangProfile.class);
                addProfile(profile, index, langsize);
                ++index;
            } catch (JSONException e) {
                throw new LangDetectException(ErrorCode.FormatError, "profile format error in '" + file.getName() + "'");
            } catch (IOException e) {
                throw new LangDetectException(ErrorCode.FileLoadError, "can't open '" + file.getName() + "'");
            } finally {
                try {
                    if (is!=null) is.close();
                } catch (IOException e) {}
            }
        }
    }
    
    /**
     * Load profiles from specified directory.
     * This method must be called once before language detection.
     *  
     * @param jsonProfiles list of JSON LangProfiles
     * @throws LangDetectException  Can't open profiles(error code = {@link ErrorCode#FileLoadError})
     *                              or profile's format is wrong (error code = {@link ErrorCode#FormatError})
     */
    public void loadProfile(List<String> jsonProfiles) throws LangDetectException {
        int index = 0;
        int langSize = jsonProfiles.size();

        if (langSize < 2) {
            throw new LangDetectException(ErrorCode.NeedLoadProfileError, "Need more than 2 profiles");
        }

        for (String json: jsonProfiles) {
            try {
                LangProfile profile = JSON.decode(json, LangProfile.class);
                addProfile(profile, index, langSize);
                ++index;
            } catch (JSONException e) {
                throw new LangDetectException(ErrorCode.FormatError, "profile format error");
            }
        }
    }

    /**
     * @param profile
     * @param langSize
     * @param index 
     * @throws LangDetectException 
     */
    private void addProfile(LangProfile profile, int index, int langSize) throws LangDetectException {
        String lang = profile.name;
        if (this.langlist.contains(lang)) {
            throw new LangDetectException(ErrorCode.DuplicateLangError, "duplicate the same language profile");
        }
        this.langlist.add(lang);
        for (String word: profile.freq.keySet()) {
            if (!this.wordLangProbMap.containsKey(word)) {
                this.wordLangProbMap.put(word, new double[langSize]);
            }
            int length = word.length();
            if (length >= 1 && length <= 3) {
                double prob = profile.freq.get(word).doubleValue() / profile.n_words[length - 1];
                this.wordLangProbMap.get(word)[index] = prob;
            }
        }
    }

    /**
     * Clear loaded language profiles (reinitialization to be available)
     */
    public DetectorFactory clear() {
        this.langlist.clear();
        this.wordLangProbMap.clear();
        return this;
    }

    /**
     * Construct Detector instance
     * 
     * @return Detector instance
     * @throws LangDetectException 
     */
    public Detector create() throws LangDetectException {
        return createDetector();
    }

    /**
     * Construct Detector instance with initial text
     *
     * @param text to detect
     * @return Detector instance
     * @throws LangDetectException
     */
    public Detector create(String text) throws LangDetectException {
        Detector detector = createDetector();
        detector.append(text);
        return detector;
    }

    /**
     * Construct Detector instance with smoothing parameter 
     * 
     * @param alpha smoothing parameter (default value = 0.5)
     * @return Detector instance
     * @throws LangDetectException 
     */
    public Detector create(double alpha) throws LangDetectException {
        Detector detector = createDetector();
        detector.setAlpha(alpha);
        return detector;
    }

    private Detector createDetector() throws LangDetectException {
        if (this.langlist.isEmpty()) {
            throw new LangDetectException(ErrorCode.NeedLoadProfileError, "need to load profiles");
        }
        Detector detector = new Detector(this);
        return detector;
    }
    
    public DetectorFactory setSeed(long seed) {
        this.seed = seed;
        return this;
    }
    
    public List<String> getLangList() {
        return Collections.unmodifiableList(this.langlist);
    }
}
