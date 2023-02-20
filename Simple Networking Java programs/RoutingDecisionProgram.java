import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * AAYUSH SHRESTHA, B00906766
 */

public class RoutingDecisionProgram {
    static String SUBNET_MASK_CLASS_A = "255.0.0.0";
    static String SUBNET_MASK_CLASS_B = "255.255.0.0";
    static String SUBNET_MASK_CLASS_C = "255.255.255.0";
    static String SUBNET_MASK_DEFAULT = "0.0.0.0";

    static ArrayList<String> SUBNET_LIST = new ArrayList<>(Arrays.asList(SUBNET_MASK_CLASS_A, SUBNET_MASK_CLASS_B, SUBNET_MASK_CLASS_C, SUBNET_MASK_DEFAULT));

    static String routingTableFilepath = "RoutingTable.txt";
    static String randomFrameFilePath = "RandomPackets.txt";
    static String routingOutputFilePath = "RandomOutput.txt";

    public static void main(String[] args) throws IOException {
        int routingTableFileLength = getTotalLineCountInFile(routingTableFilepath);
        int randomFramesFileLength = getTotalLineCountInFile(randomFrameFilePath);
        Map<String, String> routingEntryTypeMap = new HashMap<>();
        populateEntryTypeFromFileToMap(routingTableFileLength,routingEntryTypeMap);
        evaluateRandomFrames(randomFramesFileLength,routingEntryTypeMap);
    }

    private static void evaluateRandomFrames(int randomFramesFileLength, Map<String, String> routingEntryTypeMap) throws IOException {
        File file = new File(routingOutputFilePath);
        FileWriter fr = new FileWriter(file, false);
        fr.write("""
                AAYUSH SHRESTHA | B00906766
                
                """);
        String unmaskedAddress;
        for(int i=0;i<randomFramesFileLength;i++){
            String randomFrame = getDataFromFile(randomFrameFilePath,i);
            unmaskedAddress = matchUnMaskedAddressInEntryMap(randomFrame,routingEntryTypeMap);
            if(randomFrame.startsWith("127")){
                System.out.println(randomFrame+ " is loopback; discarded\n");
                fr.write(randomFrame+ " is loopback; discarded");
                fr.write("\n");
            }
            else if(SUBNET_LIST.contains(randomFrame)){
                System.out.println(randomFrame+ " is malformed; discarded\n");
                fr.write(randomFrame+ " is malformed; discarded");
                fr.write("\n");
            }
            else if(unmaskedAddress!=null){
                String[] mapValue = routingEntryTypeMap.get(unmaskedAddress).split("=",3);
                if(Objects.equals(mapValue[0], "Direct")){
                    System.out.println(randomFrame+ " will be forwarded on the directly connected network on interface " + mapValue[1]+"\n");
                    fr.write(randomFrame+ " will be forwarded on the directly connected network on interface " + mapValue[1]);
                    fr.write("\n");
                }else{
                    System.out.println(randomFrame+ " will be forwarded to "+ mapValue[2] +" out on interface "+ mapValue[1]+"\n");
                    fr.write(randomFrame+ " will be forwarded to "+ mapValue[2] +" out on interface "+ mapValue[1]);
                    fr.write("\n");
                }
            }
        }
        fr.close();
    }

    private static String matchUnMaskedAddressInEntryMap(String randomFrame, Map<String,String> routingEntryTypeMap) {
        for(String subnetMask : SUBNET_LIST){
            String address = performAndOperation(subnetMask,randomFrame);
            if(routingEntryTypeMap.containsKey(address)){
                return address;
            }
        }
        return null;
    }

    private static String performAndOperation(String randomFrame, String subnetMask) {
        StringBuilder unmaskedAddress= new StringBuilder();
        String[] ipAddressBytes=randomFrame.split("\\.");
        String[] maskBytes=subnetMask.split("\\.");

        for(int i=0;i<4;i++)
            unmaskedAddress.append(Integer.parseInt(ipAddressBytes[i])&Integer.parseInt(maskBytes[i])).append(".");

        return unmaskedAddress.toString().replaceAll(".$","");
    }

    private static void populateEntryTypeFromFileToMap(int routingTableFileLength, Map<String, String> routingEntryTypeMap) {
        String destAddress = null;
        String entryType = null;
        String interfaceId = null;
        boolean nextHopExist;
        String nextHopAddress;
        for(int i=0;i<routingTableFileLength;i+=3){
            destAddress = removeMaskFromDestAddress(getDataFromFile(routingTableFilepath,i));
            nextHopAddress = getDataFromFile(routingTableFilepath, i + 1);
            nextHopExist = !nextHopAddress.equals("-");
            interfaceId = getDataFromFile(routingTableFilepath,i+2);
            entryType = getAddressEntryType(destAddress,nextHopExist);

            routingEntryTypeMap.put(destAddress, entryType + "=" + interfaceId + "=" + nextHopAddress);
        }

    }

    private static String getAddressEntryType(String destAddress,boolean nextHopExist) {
        String lastByte = destAddress.substring(destAddress.lastIndexOf(".")+1);
        if(destAddress.equals(SUBNET_MASK_DEFAULT))
            return "Default";
        else if(!lastByte.equals("0"))
            return "Host Specific";
        else {
            if(nextHopExist)
                return "Network Specific";
            else
                return "Direct";
        }
    }

    private static String removeMaskFromDestAddress(String dataFromFile) {
        return dataFromFile.substring(0,dataFromFile.indexOf("/"));
    }

    private static int getTotalLineCountInFile(String file) throws IOException {
        int lineCount = 0;
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        while (fileReader.readLine() != null) lineCount++;
        fileReader.close();
        return lineCount;
    }

    private static String getDataFromFile(String filePath, int offset) {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.skip(offset).findFirst().stream().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}