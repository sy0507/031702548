import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lost {
  public static JSONObject addressResolution(String address) throws IOException {
//    Lost.class.getClassLoader().getResource("lostAdress");
//    File file=new File(Lost.class.getClassLoader().getResource("lostAdress").getFile());
//    String line=null;
//    while ((line=))
    ArrayList<String> level_one=new ArrayList<>();
    ArrayList<String> level_two=new ArrayList<>();
    ArrayList<String> level_three=new ArrayList<>();
    InputStream stream =  Lost.class.getClassLoader().getResourceAsStream("lostAdress");
    BufferedReader br=new BufferedReader(new InputStreamReader(stream,"utf-8"));
//    BufferedReader br=new BufferedReader(new FileReader(Lost.class.getClassLoader().getResource("lostAdress").getFile()));
    String line=null;
    int position=0;
    String[] bufstring=new String[20480];
    while((line=br.readLine())!=null) {
      bufstring[position]=line;
//      System.out.println(bufstring[position].split("\\s+")[0] + "------" + bufstring[position].split("\\s+")[1]);
      String[] a=bufstring[position].split("\\s+");
      if (a[0].substring(2).equals("0000")) {
        level_one.add(a[1]);

      }
      else if (a[0].substring(4).equals("00")){
        level_two.add(a[1]);
      }
      else {
        level_three.add(a[1]);
      }
      position++;
    }
//    for (String s : level_one){
//      System.out.println(s);
//    }
    br.close();//关闭文件
//    for(int i=0;i<position;i++) {
//      System.out.println(bufstring[i]);
//    }
    JSONObject jsonObject=new JSONObject();
    String aString=address;
    String splits[] = aString.split(",");
    String name= splits[0];
    jsonObject.put("姓名",name);
    address=address.replace(name,"");
    address=address.replace(",","");
    address=address.replace(".","");
    String phoneRegex = "\\d{11}";
    Matcher a = Pattern.compile(phoneRegex).matcher(address);
    String phoneNumber=null;
    while (a.find()){
      phoneNumber=a.group();
      address=address.replace(phoneNumber,"");
    }
    jsonObject.put("手机",phoneNumber);
    String regex="(?<province>[^省]+自治区|.*?省|.*?行政区|)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|)(?<county>[^县]+县|.+?区|.+市|.+旗|.+海域|.+岛)?(?<town>.+镇|.+街道)?(?<road>.*街|.*路|.*巷)?(?<number>[\\d]+号|)?(?<village>.*)";
    Matcher m=Pattern.compile(regex).matcher(address);
    String province=null,city=null,county=null,town=null,road=null,number=null,village=null;
    JSONArray jsonArray=new JSONArray();
    if(m.find()){
//      row=new LinkedHashMap<String,String>();
      city=m.group("city");
      if (city.equals("北京市")||city.equals("上海市")||city.equals("重庆市")||city.equals("天津市")) {
        jsonArray.put(city.substring(0,city.length()-1));
      }//特殊判断直辖市
      else{
        province = m.group("province");
        if (province.equals("")) {
//          System.out.println("okkk");
          for (String s : level_one){

            if (s.substring(0,2).equals(address.substring(0,2))) {
              jsonArray.put(province=s);
              if (province.substring(province.length()-1,province.length()).equals("省"))
              {address=address.replace(province.substring(0,province.length()-1),"");}
              if (province.substring(province.length()-3,province.length()).equals("自治区")) {
                address=address.replace(province.substring(0,province.length()-3),"");
              }
              if (province.substring(province.length()-3,province.length()).equals("行政区")) {
                address=address.replace(province.substring(0,province.length()-3),"");
              }
            }
          }
          if (province.equals("")) {
            jsonArray.put(province="");
          }
        }

        else {
          jsonArray.put(province == null ? "" : province.trim());
        }
      }
//      System.out.println(address);
      String city_one=null;
      Matcher c=Pattern.compile(regex).matcher(address);
      if(c.find()) {
        city_one = c.group("city");
        if (city_one.equals("")) {
          for (String i : level_two){
            if (i.substring(0,2).equals(address.substring(0,2))) {
              jsonArray.put(city_one=i);
              if (city_one.substring(city_one.length()-1,city_one.length()).equals("市")||city_one.substring(city_one.length()-1,city_one.length()).equals("盟"))
              {address=address.replace(city_one.substring(0,city_one.length()-1),"");}
              if (city_one.substring(city_one.length()-3,city_one.length()).equals("自治州")) {
                address=address.replace(city_one.substring(0,city_one.length()-3),"");
              }


            }

          }
          if (city_one.equals("")) {
            jsonArray.put(city_one="");
          }

        }
        else{
          jsonArray.put(city_one == null ? "" : city_one.trim());}
        String county_one=null;
        Matcher b=Pattern.compile(regex).matcher(address);
        if (b.find()) {
          county_one=b.group("county");
          if (city_one.equals("")) {
            for (String i : level_three){
              if (i.substring(0,2).equals(address.substring(0,2))) {
                jsonArray.put(county_one=i);
                if (county_one.substring(county_one.length()-1,county_one.length()).equals("市")||county_one.substring(county_one.length()-1,county_one.length()).equals("区")||county_one.substring(county_one.length()-1,county_one.length()).equals("旗"))
                {address=address.replace(county_one.substring(0,county_one.length()-1),"");}
                if (county_one.substring(county_one.length()-3,county_one.length()).equals("自治州")) {
                  address=address.replace(county_one.substring(0,county_one.length()-3),"");
                }

              }

            }
            if (county_one.equals("")) {
              jsonArray.put(county_one="");
            }

          }
          else {
            jsonArray.put(county_one == null ? "" : county_one.trim());}
        }

        town = m.group("town");
        jsonArray.put(town == null ? "" : town.trim());
        road=m.group("road");
        jsonArray.put(road==null?"":road.trim());
        number=m.group("number");
        jsonArray.put(number==null?"":number.trim());
        village = m.group("village");
        jsonArray.put(village == null ? "" : village.trim());
      }
    }
    jsonObject.put("地址",jsonArray);
    return  jsonObject;
  }

  public static void main(String[] args) throws IOException {

    JSONArray jsonArray=new JSONArray();
//    jsonArray.put(addressResolution("李四,福建省福州13756899511市鼓楼区鼓西街道湖滨路110号湖滨大厦一层."));
//    jsonArray.put(addressResolution("张三,福建福州闽13599622362侯县上街镇福州大学10#111."));
//    jsonArray.put(addressResolution("王五,福建省福州市鼓楼18960221533区五一北路123号福州鼓楼医院."));
//    jsonArray.put(addressResolution("小美,北京市东15822153326城区交道口东大街1号北京市东城区人民法院."));
//    jsonArray.put(addressResolution("小陈,广东省东莞市凤岗13965231525镇凤平路13号."));
    File fin = new File("1.txt");
    FileInputStream in=new FileInputStream(fin);
    BufferedReader br=new BufferedReader((new InputStreamReader(in, "UTF-8")));
    String line=null;
    int position=0;
    String[] bufstring=new String[20480];
    while((line=br.readLine())!=null) {
      bufstring[position]=line;


      position++;
    }

    br.close();//关闭文件
//    for(int i=0;i<position;i++) {
//      System.out.println(bufstring[i]);}
    for (int i=0;i<position;i++){

      jsonArray.put(addressResolution(bufstring[i]));
    }
   File file=new File("2.txt");
    FileOutputStream out=new FileOutputStream(file,true);

    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
    bw.write(jsonArray.toString());

    System.out.println(jsonArray.toString());

    bw.flush();
    System.out.println(jsonArray);
  }
}
