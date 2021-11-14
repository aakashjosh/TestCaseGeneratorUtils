import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestCaseGeneratorMainClass {
    public static void main(String[] args) throws Exception {
        String s = "{\"menu\": {  \n" +
                "  \"menu_id\": \"file\",  \n" +
                "  \"menu_value\": \"File\",  \n" +
                "  \"popup\": {  \n" +
                "    \"menuitem\": [  \n" +
                "      {\"value\": \"New\", \"onclick\": \"CreateDoc()\"},  \n" +
                "      {\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},  \n" +
                "      {\"value\": \"Save\", \"onclick\": \"SaveDoc()\"}  \n" +
                "    ]  \n" +
                "  }  \n" +
                "}}";
        TestCaseGeneratorUtil obj = new TestCaseGeneratorUtil(s);
        JSONObject o = obj.getOriginalJsonObject();
        String orgJson = obj.getOriginalJsonObjectString();
        obj.addTestCasesForPresentKey("/menu/menu_id", Arrays.asList("NewFile", "NextFile"));
        obj.addTestCasesForAbsentKey("/menu/new_menu_id",Arrays.asList("NewlyAddedFile", "AddedFile","/add/newAdd") );
        System.out.println(obj.addTestCasesForPresentKey("menu_id", Arrays.asList("NewFile", "NextFile")));//will return false
        System.out.println(obj.addTestCasesForAbsentKey("/menu/menu_id", Arrays.asList("NewFile", "NextFile")));//will return false
        obj.changeIsMandatoryStatusForKey("/menu/menu_id");
        obj.changeIsMandatoryStatusForKey("/menu/menu_value");
        Map<String, String> m = obj.getAllKeysWithValues();
        Map<String, String> m2 = obj.getDefaultValueForKeys();
        Map<String, Boolean> m3= obj.getIsKeyMandatoryStatusForAllKeys();
        List<String> l1= new ArrayList<>();
        l1.add("/menu/menu_id");l1.add("/menu/new_added_menu_id");
        List<String> l2= new ArrayList<>();l2.add("Hi");
        List<String> l3 = new ArrayList<>(); l3.add("Hi there"); l3.add("Hello there");
        List<List<String>> t = new ArrayList<>();
        t.add(l2);t.add(l3);
        obj.addTestCasesForAbsentKeys(l1, t);


        List<String> a1= new ArrayList<>();
        a1.add("/menu/menu_id");a1.add("/menu/new_added_menu_id");
        List<String> a2= new ArrayList<>();a2.add("Hi");
        List<String> a3 = new ArrayList<>(); a3.add("Hi there"); a3.add("Hello there");
        List<List<String>> z = new ArrayList<>();
        z.add(a2);t.add(a3);
        obj.addTestCasesForPresentKeys(a1, z);
        List<String> list = obj.getAllTestCases("prefix", "suffix");

        System.out.println(list.size());
    }
}
