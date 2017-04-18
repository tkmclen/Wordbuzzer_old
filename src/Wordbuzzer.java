import java.io.BufferedReader;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import static java.lang.Character.isLetter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Geoff
 */

public class Wordbuzzer extends javax.swing.JFrame {
    
    public class Posting {
        int id;
        String name;
        String skills;
        
        public Posting(){
            id = -1;
            name = new String();
            skills = new String();
        }
    }
    public class Phrase {
        String words;
        int count;
        
        public Phrase(){
            words = new String();
            count = 0;
        }
        
        public Phrase(String str){
            words = str;
            count = 0;
        }
        
        public Phrase(String str, int c){
            words = str;
            count = c;
        }
        
        public Boolean equals(String str){
            if (this.words.equals(str))
                return true;
            else
                return false;
        }
        
        public void inc(){
            this.count++;
        }
    }
    
    String[] stopwords = new String[]{"a",  "an", "and", "are", "as", "at",
                                      "be", "by", "for", "has", "he", "in",
                                      "is", "it", "its", "of",  "on", "that",
                                      "the", /**/ "to",  "was", "were"};
    
    Hashtable<String, Integer> exclusionHash; 
    String excludeThis;
    
    ArrayList<Posting> entryList;
    Posting currentEntry;
    HashMap<String, Integer> h1;
    HashMap<String, Integer> h2;
    HashMap<String, Integer> h3;
    ArrayList<Phrase> p1;
    ArrayList<Phrase> p2;
    ArrayList<Phrase> p3;
    
    public Wordbuzzer() {
        initComponents();
        exclusionHash = new Hashtable<>(); 
        excludeThis = null;
        Posting currentEntry = new Posting();
        entryList = new ArrayList<Posting>();
        
        loadStopWords();
        
        HashMap<String, Integer> hash1 = new HashMap<String, Integer>();
        HashMap<String, Integer> hash2 = new HashMap<String, Integer>();
        HashMap<String, Integer> hash3 = new HashMap<String, Integer>();
        ArrayList<Phrase> p1 = new ArrayList<Phrase>();
        ArrayList<Phrase> p2 = new ArrayList<Phrase>();
        ArrayList<Phrase> p3 = new ArrayList<Phrase>();
    }

    ///Tom methods////////////////////////////////
    /*public void findDirectory(){
        int returnVal = STOPSAVE_CHOOSER.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            File file = STOPSAVE_CHOOSER.getSelectedFile();
            FOLDER_PATH_TEXT.setText(file.getPath());
            directory = file.getAbsolutePath();
        }
    }*/
    
    public void loadStopWords(){
        for(int i = 0; i < stopwords.length; i++){
            exclusionHash.put(stopwords[i], exclusionHash.size());
            STOPLIST.add(stopwords[i], WIDTH);
        }
    }
    public void addStopWord(){
        String word = NEW_STOPWORD.getText();
        exclusionHash.put(word, exclusionHash.size());
        STOPLIST.add(word, WIDTH);
    }
    
    public void removeStopWord(){
       String word = STOPLIST.getSelectedItem();
        exclusionHash.remove(word);
        STOPLIST.remove(word);}
    
    public void saveStoplist(){
        String strout = Integer.toString(STOPLIST.getItemCount());
        for(int i = 0; i < STOPLIST.getItemCount(); i++){
            strout = strout + "|" + STOPLIST.getItem(i);
        }
        int returnVal = STOPSAVE_CHOOSER.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            try(FileWriter fw = new FileWriter(STOPSAVE_CHOOSER.getSelectedFile()+".txt")){
            fw.write(strout);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    
   ////////////////////// public void loadStoplist(){}
    
    
    
    public void analyze(){
        SINGLES.removeAll();
        PAIRS.removeAll();
        TRIPLETS.removeAll();
        p1.clear();
        p2.clear();
        p3.clear();
        h1.clear();
        h2.clear();
        h3.clear();
        
        for(Posting entry: entryList){
            
            ArrayList<String> s1 = new ArrayList<String>();
            ArrayList<String> s2 = new ArrayList<String>();
            ArrayList<String> s3 = new ArrayList<String>();
            
            String[] newLines = entry.skills.toLowerCase().split("\n");
            for(int i = 0; i < newLines.length; i++){
                String[] words = newLines[i].split(" ");
                for(int w = 0; w < words.length; w++){
                    String word = words[w].replaceAll("[^a-zA-Z0-9-]", "");
                    if(!exclusionHash.contains(word) && !s1.contains(word)){
                        s1.add(word);
                        if((words.length - w) > 0 &&
                                words[w].endsWith("[^a-zA-Z0-9-]")){ //careful--this one checks the pre-cleaned word for phrase ending
                            String nextword = words[w+1].replaceAll("[^a-zA-Z0-9-]", "");
                            if(!exclusionHash.contains(nextword)){
                                String phrase2 = word + " " + nextword;
                                if(!s2.contains(phrase2))
                                    s2.add(phrase2);
                            }
                            if((words.length - w) > 1 &&
                                    words[w+1].endsWith("[^a-zA-Z0-9-]")){
                                String nextnextword = words[w+2].replaceAll("[^a-zA-Z0-9-]", "");
                                String phrase3 = word + " " + nextword + " " + nextnextword;
                                if(!s3.contains(phrase3))
                                    s3.add(phrase3);
                            }
                        }
                    }
                }
            } 
            addEntry(s1, s2, s3);
        }
        displayResults();
    }
    
    public void addEntry(ArrayList<String> s1, ArrayList<String> s2, ArrayList<String> s3){
        for(String s : s1){
            if(h1.containsKey(s)){
                int i = h1.get(s);
                p1.get(i).inc();;
            }
            else{
                Phrase temp = new Phrase(s, 1);
                p1.add(temp);
                int i = p1.indexOf(temp);
                h1.put(s, i);
            }
                
        }
        
        for(String s : s2){
            if(h2.containsKey(s)){
                int i = h2.get(s);
                p2.get(i).inc();;
            }
            else{
                Phrase temp = new Phrase(s, 1);
                p2.add(temp);
                int i = p2.indexOf(temp);
                h2.put(s, i);
            }
              
        }
        
        for(String s : s3){
            if(h3.containsKey(s)){
                int i = h3.get(s);
                p3.get(i).inc();;
            }
            else{
                Phrase temp = new Phrase(s, 1);
                p3.add(temp);
                int i = p3.indexOf(temp);
                h3.put(s, i);
            }
              
        }
    }
    
    public void displayResults(){
        /*int i = 0;
        if(val == null)
            val = 0;

                System.out.println("str: " + str + "    val: " + Integer.toString(val) + "    hashNum: " + Integer.toString(hashNum));

        
        if(hashNum == 1){
            for(; i < SINGLES.getItemCount(); i++){
                String[] split = SINGLES.getItem(i).split(": ");
                if(split[0] == null || split[1] == null || val == null)
                    continue;
                if(val > Integer.getInteger(split[1]))
                    break;
            }
            SINGLES.add(str + ": " + val.toString(), 0);
        }
        
        else if(hashNum == 2){
            for(; i < PAIRS.getItemCount(); i++){
                String[] split = PAIRS.getItem(i).split(": ");
                if(val > Integer.getInteger(split[1]))
                    break;
            }
            PAIRS.add(str + ": " + val.toString(), 0);  
        }
        
        else
        {
            for(; i < TRIPLETS.getItemCount(); i++){
                String[] split = TRIPLETS.getItem(i).split(": ");
                if(val > Integer.getInteger(split[1]))
                    break;
            }
            TRIPLETS.add(str + ": " + val.toString(), 0);
        }*/
    }
    
    public void saveResults(){
        String strout = "Single Words: ";
        for(int i = 0; i < SINGLES.getItemCount(); i++){
            strout = strout + "\n" + SINGLES.getItem(i);
        }
        strout = strout + "\n2-Word Phrases:";
        for(int i = 0; i < PAIRS.getItemCount(); i++){
            strout = strout + "\n" + PAIRS.getItem(i);
        }
        strout = strout + "\n3-Word Phrases:";
        for(int i = 0; i < TRIPLETS.getItemCount(); i++){
            strout = strout + "\n" + TRIPLETS.getItem(i);
        }
        
        int returnVal = RESULTSAVE_CHOOSER.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            try(FileWriter fw = new FileWriter(RESULTSAVE_CHOOSER.getSelectedFile()+".txt")){
            fw.write(strout);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        STOPSAVE_CHOOSER = new javax.swing.JFileChooser();
        STOPLOAD_CHOOSER = new javax.swing.JFileChooser();
        RESULTSAVE_CHOOSER = new javax.swing.JFileChooser();
        MAIN_PANEL_1 = new javax.swing.JPanel();
        STOPLIST = new java.awt.List();
        NEW_STOPWORD = new java.awt.TextField();
        ADD_STOPWORD = new java.awt.Button();
        REMOVE_STOPWORD = new java.awt.Button();
        SAVE_STOPLIST = new javax.swing.JButton();
        WORDSNOTCOUNTED = new javax.swing.JLabel();
        TITLE_AUTHOR_PANEL = new javax.swing.JPanel();
        TITLE = new javax.swing.JLabel();
        AUTHOR = new javax.swing.JLabel();
        LOAD_STOPLIST = new javax.swing.JButton();
        MAIN_PANEL_2 = new javax.swing.JPanel();
        DELETE_ENTRY = new java.awt.Button();
        NEW_ENTRY = new java.awt.Button();
        SKILLS_ENTRY = new java.awt.TextArea();
        SAVE_ENTRY = new java.awt.Button();
        ENTRY_NAME = new java.awt.TextField();
        ENTRY_LIST = new java.awt.List();
        POSTINGS_LABEL = new java.awt.Label();
        MAIN_PANEL_3 = new javax.swing.JPanel();
        OCCURRENCE_HEADER = new javax.swing.JLabel();
        ANALYZE = new javax.swing.JButton();
        SINGLES = new java.awt.List();
        TRIPLETS = new java.awt.List();
        PAIRS = new java.awt.List();
        SAVE_RESULTS = new javax.swing.JButton();

        STOPSAVE_CHOOSER.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        STOPSAVE_CHOOSER.setDialogTitle("");
        STOPSAVE_CHOOSER.setFileHidingEnabled(false);

        STOPLOAD_CHOOSER.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        RESULTSAVE_CHOOSER.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        STOPLIST.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        STOPLIST.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        NEW_STOPWORD.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        NEW_STOPWORD.setName("BADWORD"); // NOI18N

        ADD_STOPWORD.setLabel("^^  Add  ^^");
        ADD_STOPWORD.setName("EXCLUDE"); // NOI18N
        ADD_STOPWORD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ADD_STOPWORDActionPerformed(evt);
            }
        });

        REMOVE_STOPWORD.setLabel("<<   Remove");
        REMOVE_STOPWORD.setName("REMOVEEXCLUSION"); // NOI18N
        REMOVE_STOPWORD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                REMOVE_STOPWORDActionPerformed(evt);
            }
        });

        SAVE_STOPLIST.setText("Save list...");
        SAVE_STOPLIST.setName("SAVEEXCLUDE"); // NOI18N
        SAVE_STOPLIST.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SAVE_STOPLISTActionPerformed(evt);
            }
        });

        WORDSNOTCOUNTED.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        WORDSNOTCOUNTED.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        WORDSNOTCOUNTED.setText("Ignore List:");

        TITLE.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        TITLE.setText("WORDBUZZER");

        AUTHOR.setText("by Tom ^_^");

        javax.swing.GroupLayout TITLE_AUTHOR_PANELLayout = new javax.swing.GroupLayout(TITLE_AUTHOR_PANEL);
        TITLE_AUTHOR_PANEL.setLayout(TITLE_AUTHOR_PANELLayout);
        TITLE_AUTHOR_PANELLayout.setHorizontalGroup(
            TITLE_AUTHOR_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TITLE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(TITLE_AUTHOR_PANELLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(AUTHOR))
        );
        TITLE_AUTHOR_PANELLayout.setVerticalGroup(
            TITLE_AUTHOR_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TITLE_AUTHOR_PANELLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(TITLE, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AUTHOR))
        );

        LOAD_STOPLIST.setText("Load list...");
        LOAD_STOPLIST.setName("SAVEEXCLUDE"); // NOI18N

        javax.swing.GroupLayout MAIN_PANEL_1Layout = new javax.swing.GroupLayout(MAIN_PANEL_1);
        MAIN_PANEL_1.setLayout(MAIN_PANEL_1Layout);
        MAIN_PANEL_1Layout.setHorizontalGroup(
            MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                .addGroup(MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(WORDSNOTCOUNTED, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(STOPLIST, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(REMOVE_STOPWORD, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(NEW_STOPWORD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(SAVE_STOPLIST, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LOAD_STOPLIST, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(ADD_STOPWORD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                .addComponent(TITLE_AUTHOR_PANEL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );
        MAIN_PANEL_1Layout.setVerticalGroup(
            MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                .addComponent(TITLE_AUTHOR_PANEL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(WORDSNOTCOUNTED, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                        .addComponent(NEW_STOPWORD, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ADD_STOPWORD, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                        .addComponent(REMOVE_STOPWORD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(103, 103, 103)
                        .addComponent(SAVE_STOPLIST)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LOAD_STOPLIST)
                        .addGap(67, 67, 67))
                    .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(STOPLIST, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        DELETE_ENTRY.setLabel("DELETE");
        DELETE_ENTRY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DELETE_ENTRYActionPerformed(evt);
            }
        });

        NEW_ENTRY.setLabel("NEW");
        NEW_ENTRY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NEW_ENTRYActionPerformed(evt);
            }
        });

        SKILLS_ENTRY.setText("Copy/paste/reformat \"Preferred skills\" list with a new line for each entry:\n- List item 1\n- List item 2\n         ...\n         ...\n         ...\n- Final list item");

        SAVE_ENTRY.setLabel("SAVE");
        SAVE_ENTRY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SAVE_ENTRYActionPerformed(evt);
            }
        });

        ENTRY_NAME.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        ENTRY_NAME.setText("Position Name");

        ENTRY_LIST.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        ENTRY_LIST.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ENTRY_LISTItemStateChanged(evt);
            }
        });

        POSTINGS_LABEL.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        POSTINGS_LABEL.setText("Job Postings:");

        javax.swing.GroupLayout MAIN_PANEL_2Layout = new javax.swing.GroupLayout(MAIN_PANEL_2);
        MAIN_PANEL_2.setLayout(MAIN_PANEL_2Layout);
        MAIN_PANEL_2Layout.setHorizontalGroup(
            MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MAIN_PANEL_2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MAIN_PANEL_2Layout.createSequentialGroup()
                        .addComponent(DELETE_ENTRY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NEW_ENTRY, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(141, 141, 141)
                        .addComponent(SAVE_ENTRY, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(MAIN_PANEL_2Layout.createSequentialGroup()
                        .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ENTRY_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(POSTINGS_LABEL, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SKILLS_ENTRY, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ENTRY_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MAIN_PANEL_2Layout.setVerticalGroup(
            MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MAIN_PANEL_2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ENTRY_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(POSTINGS_LABEL, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ENTRY_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                    .addComponent(SKILLS_ENTRY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(DELETE_ENTRY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(NEW_ENTRY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(SAVE_ENTRY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        OCCURRENCE_HEADER.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        OCCURRENCE_HEADER.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        OCCURRENCE_HEADER.setText("Word Occurrence:");
        OCCURRENCE_HEADER.setName("WORDBANNER"); // NOI18N

        ANALYZE.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        ANALYZE.setText("ANALYZE");
        ANALYZE.setName("ANALYZE"); // NOI18N
        ANALYZE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ANALYZEActionPerformed(evt);
            }
        });

        SINGLES.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        TRIPLETS.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        PAIRS.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        SAVE_RESULTS.setText("Save to file...");
        SAVE_RESULTS.setName("SAVEEXCLUDE"); // NOI18N
        SAVE_RESULTS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SAVE_RESULTSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MAIN_PANEL_3Layout = new javax.swing.GroupLayout(MAIN_PANEL_3);
        MAIN_PANEL_3.setLayout(MAIN_PANEL_3Layout);
        MAIN_PANEL_3Layout.setHorizontalGroup(
            MAIN_PANEL_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MAIN_PANEL_3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MAIN_PANEL_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MAIN_PANEL_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ANALYZE, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MAIN_PANEL_3Layout.createSequentialGroup()
                            .addComponent(SAVE_RESULTS)
                            .addGap(10, 10, 10)))
                    .addComponent(OCCURRENCE_HEADER, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SINGLES, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PAIRS, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TRIPLETS, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MAIN_PANEL_3Layout.setVerticalGroup(
            MAIN_PANEL_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MAIN_PANEL_3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MAIN_PANEL_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MAIN_PANEL_3Layout.createSequentialGroup()
                        .addComponent(TRIPLETS, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(SINGLES, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(MAIN_PANEL_3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(OCCURRENCE_HEADER)
                        .addGap(18, 18, 18)
                        .addComponent(ANALYZE, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SAVE_RESULTS)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(PAIRS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(MAIN_PANEL_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(MAIN_PANEL_2, javax.swing.GroupLayout.PREFERRED_SIZE, 586, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MAIN_PANEL_3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MAIN_PANEL_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(MAIN_PANEL_2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(MAIN_PANEL_3, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ANALYZEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ANALYZEActionPerformed
        analyze();
    }//GEN-LAST:event_ANALYZEActionPerformed

    private void REMOVE_STOPWORDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_REMOVE_STOPWORDActionPerformed
        removeStopWord();
    }//GEN-LAST:event_REMOVE_STOPWORDActionPerformed

    private void ADD_STOPWORDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ADD_STOPWORDActionPerformed
        addStopWord();
    }//GEN-LAST:event_ADD_STOPWORDActionPerformed

    private void SAVE_STOPLISTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SAVE_STOPLISTActionPerformed
        saveStoplist();
    }//GEN-LAST:event_SAVE_STOPLISTActionPerformed
/**/
    private void SAVE_RESULTSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SAVE_RESULTSActionPerformed
        saveResults();
    }//GEN-LAST:event_SAVE_RESULTSActionPerformed

    private void DELETE_ENTRYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DELETE_ENTRYActionPerformed
        int temp = ENTRY_LIST.getSelectedIndex();
        for (Posting entry : entryList) {
            if (entry.id > temp)
                entry.id = entry.id - 1;
        }
        entryList.remove(temp);
        ENTRY_LIST.remove(temp);
        Posting entry = new Posting();
        ENTRY_NAME.setText("Position Name");
        SKILLS_ENTRY.setText(null);
        currentEntry = entry;
        ENTRY_LIST.deselect(ENTRY_LIST.getSelectedIndex());
    }//GEN-LAST:event_DELETE_ENTRYActionPerformed

    private void SAVE_ENTRYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SAVE_ENTRYActionPerformed
        currentEntry.name = ENTRY_NAME.getText();
        currentEntry.skills = SKILLS_ENTRY.getText();
        if(currentEntry.id == -1){
            currentEntry.id = entryList.size();
            entryList.add(currentEntry);
            ENTRY_LIST.add(currentEntry.name);
        }
        else{
            entryList.set(currentEntry.id, currentEntry);
            ENTRY_LIST.getSelectedItem().equals(currentEntry.name);
        }
        Posting entry = new Posting();
        ENTRY_NAME.setText("Position Name");
        SKILLS_ENTRY.setText(null);
        currentEntry = entry;
        ENTRY_LIST.deselect(ENTRY_LIST.getSelectedIndex());
    }//GEN-LAST:event_SAVE_ENTRYActionPerformed

    private void NEW_ENTRYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NEW_ENTRYActionPerformed
        Posting entry = new Posting();
        ENTRY_NAME.setText("Position Name");
        SKILLS_ENTRY.setText(null);
        currentEntry = entry;
        ENTRY_LIST.deselect(ENTRY_LIST.getSelectedIndex());
    }//GEN-LAST:event_NEW_ENTRYActionPerformed

    private void ENTRY_LISTItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ENTRY_LISTItemStateChanged
        currentEntry = entryList.get(ENTRY_LIST.getSelectedIndex());
        ENTRY_NAME.setText(currentEntry.name);
        SKILLS_ENTRY.setText(currentEntry.skills);
    }//GEN-LAST:event_ENTRY_LISTItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Wordbuzzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Wordbuzzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Wordbuzzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Wordbuzzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Wordbuzzer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button ADD_STOPWORD;
    private javax.swing.JButton ANALYZE;
    private javax.swing.JLabel AUTHOR;
    private java.awt.Button DELETE_ENTRY;
    private java.awt.List ENTRY_LIST;
    private java.awt.TextField ENTRY_NAME;
    private javax.swing.JButton LOAD_STOPLIST;
    private javax.swing.JPanel MAIN_PANEL_1;
    private javax.swing.JPanel MAIN_PANEL_2;
    private javax.swing.JPanel MAIN_PANEL_3;
    private java.awt.Button NEW_ENTRY;
    private java.awt.TextField NEW_STOPWORD;
    private javax.swing.JLabel OCCURRENCE_HEADER;
    private java.awt.List PAIRS;
    private java.awt.Label POSTINGS_LABEL;
    private java.awt.Button REMOVE_STOPWORD;
    private javax.swing.JFileChooser RESULTSAVE_CHOOSER;
    private java.awt.Button SAVE_ENTRY;
    private javax.swing.JButton SAVE_RESULTS;
    private javax.swing.JButton SAVE_STOPLIST;
    private java.awt.List SINGLES;
    private java.awt.TextArea SKILLS_ENTRY;
    private java.awt.List STOPLIST;
    private javax.swing.JFileChooser STOPLOAD_CHOOSER;
    private javax.swing.JFileChooser STOPSAVE_CHOOSER;
    private javax.swing.JLabel TITLE;
    private javax.swing.JPanel TITLE_AUTHOR_PANEL;
    private java.awt.List TRIPLETS;
    private javax.swing.JLabel WORDSNOTCOUNTED;
    // End of variables declaration//GEN-END:variables
}

