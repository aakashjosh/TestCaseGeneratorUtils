import org.json.JSONObject;

import java.util.*;

public class TestCaseGeneratorUtil {
    private static final boolean defaultIsKeyMandatoryFlag= true;
    private static final String STRING_OPERATION = new String("op");
    private static final String REPLACE_OPERATION = new String("replace");
    private static final String ADD_OPERATION = new String("add");
    private static final String PATH_OPERATION = new String("path");
    private static final String STRING_VALUE = new String("value");
    private JSONObject originalJsonStructure = null;
    private Map<String, String> defaultValueForKeyMap  = null,fullPathToValueMap = null;
    private Map<String, Boolean> isKeyMandatoryMap = null;
    private Map<String, List<String>> allValidValuesForKeyMap =null;
    TestCaseGeneratorUtil(String jsonString) throws Exception {
        originalJsonStructure = new JSONObject(jsonString);
        isKeyMandatoryMap = new HashMap<>();
        defaultValueForKeyMap = new HashMap<>();
        allValidValuesForKeyMap = new HashMap<>();
        fullPathToValueMap= new HashMap<>();
        initializeMaps(originalJsonStructure, "");
        System.out.println("Maps Initialized");
    }
    public List<String> getAllTestCases(String prefix, String suffix){
        List<String> allTestCases = new ArrayList<>();
        getAllTestCases("", prefix, suffix, 0, new ArrayList<>(this.allValidValuesForKeyMap.keySet()), allTestCases);
        return allTestCases;
    }

    private void getAllTestCases(String curr, String prefix, String suffix, int i, List<String>keys, List<String> allTestCases){
        if(i == keys.size()){
            allTestCases.add(prefix+curr+suffix);
            return;
        }
        if(!this.allValidValuesForKeyMap.containsKey(keys.get(i)))
            getAllTestCases(curr, prefix, suffix, i+1, keys, allTestCases);
        if(this.isKeyMandatoryMap.containsKey(keys.get(i))){
            for(String test : this.allValidValuesForKeyMap.get(keys.get(i))){
                String newTestScenario = curr.length() ==0 ? "" : curr + ",\n";
                newTestScenario +="{\n";
                newTestScenario+="\""+STRING_OPERATION+"\" : \""+REPLACE_OPERATION+"\"";newTestScenario+=",\n";
                newTestScenario+="\""+PATH_OPERATION+"\" : \""+(keys.get(i))+"\"";newTestScenario+=",\n";
                newTestScenario+="\""+STRING_VALUE+"\" : \""+test+"\"";newTestScenario+="\n";
                newTestScenario+="}";
                getAllTestCases(newTestScenario, prefix, suffix, i+1, keys, allTestCases);
            }
            if(!this.isKeyMandatoryMap.get(keys.get(i)))
                getAllTestCases(curr, prefix, suffix, i+1, keys, allTestCases);
        }
        else{
            for(String test : this.allValidValuesForKeyMap.get(keys.get(i))){
                String newTestScenario = curr.length() ==0 ? "" : curr + ",\n";
                newTestScenario +="{\n";
                newTestScenario+="\""+STRING_OPERATION+"\" : \""+ADD_OPERATION+"\"";newTestScenario+=",\n";
                newTestScenario+="\""+PATH_OPERATION+"\" : \""+(keys.get(i))+"\"";newTestScenario+=",\n";
                newTestScenario+="\""+STRING_VALUE+"\" : \""+test+"\"";newTestScenario+="\n";
                newTestScenario+="}";
                getAllTestCases(newTestScenario, prefix, suffix, i+1, keys, allTestCases);
            }
            getAllTestCases(curr, prefix, suffix, i+1, keys, allTestCases);
        }
    }


    private void initializeMaps(JSONObject json, String parentAddress){
        Set<String> keysForCurrentJsonObj = json.keySet();
        for(String key : keysForCurrentJsonObj){
            String currKeyAddress = getWholeAddressForKey(key, parentAddress);
            String value = json.optString(key);
            boolean isJsonArray = json.optJSONArray(key)!=null;
            if(json.optJSONObject(key)!= null && !isJsonArray)
                initializeMaps(json.getJSONObject(key),currKeyAddress);
            else if(!isJsonArray){
                this.isKeyMandatoryMap.put(currKeyAddress, defaultIsKeyMandatoryFlag);
                this.defaultValueForKeyMap.put(currKeyAddress, value);
                this.fullPathToValueMap.put(currKeyAddress, value);
                this.allValidValuesForKeyMap.putIfAbsent(currKeyAddress, new ArrayList<>());
                this.allValidValuesForKeyMap.get(currKeyAddress).add(value);
            }

        }
    }
    private String getWholeAddressForKey(String key, String parentAddress){
        return parentAddress + "/" + key;
    }
    public JSONObject getOriginalJsonObject(){
        return new JSONObject(getOriginalJsonObjectString());
    }
    public String getOriginalJsonObjectString(){
        return new String(this.originalJsonStructure.toString());
    }
    public Map<String, String> getAllKeysWithValues(){
        return new HashMap<>(this.fullPathToValueMap);
    }
    public Map<String, String> getDefaultValueForKeys(){
        return new HashMap<>(this.defaultValueForKeyMap);
    }
    public Map<String, Boolean> getIsKeyMandatoryStatusForAllKeys(){
        return new HashMap<>(this.isKeyMandatoryMap);
    }
    public boolean changeIsMandatoryStatusForKey(String key){
        if(!this.isKeyMandatoryMap.containsKey(key))
            return false;
        this.isKeyMandatoryMap.put(key, !this.isKeyMandatoryMap.get(key));
        return true;
    }
    public boolean addTestCasesForPresentKey(String key, List<String> possibleValues){
        if(!this.allValidValuesForKeyMap.containsKey(key))
            return false;
        this.allValidValuesForKeyMap.get(key).addAll(possibleValues);
        return true;
    }
    public void addTestCasesForPresentKeys(List<String > keys, List<List<String>> possibleTestCases){
        int min = Math.min(keys.size(), possibleTestCases.size());
        for(int i =0; i<min; i++){
            if(this.allValidValuesForKeyMap.containsKey(keys.get(i))){
                this.allValidValuesForKeyMap.get(keys.get(i)).addAll(possibleTestCases.get(i));
            }
        }
    }

    public boolean addTestCasesForAbsentKey(String key, List<String> possibleValues){
        if(this.allValidValuesForKeyMap.containsKey(key))
            return false;
        this.allValidValuesForKeyMap.put(key, new ArrayList<>());
        this.allValidValuesForKeyMap.get(key).addAll(possibleValues);
        return true;
    }

    public void addTestCasesForAbsentKeys(List<String> keys, List<List<String>> possibleTestCases){
        int min = Math.min(keys.size(), possibleTestCases.size());
       for(int i =0; i<min; i++){
           if(!this.allValidValuesForKeyMap.containsKey(keys.get(i))){
               this.allValidValuesForKeyMap.put(keys.get(i), new ArrayList<>());
               this.allValidValuesForKeyMap.get(keys.get(i)).addAll(possibleTestCases.get(i));
           }
       }
    }
}
