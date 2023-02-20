import java.util.Scanner;

public class CRCErrorDetectionProgram {
    static String transmittedBitStream = "";
    static int divisorLength = 0;

    public static void main(String[] args) {
        senderFunction();
    }

    private static void senderFunction() {
        System.out.println("Enter Divisor G(x) :  ");
        Scanner sc = new Scanner(System.in);
        String divisor = sc.next();
        divisorLength = divisor.length();
        System.out.println("Enter Dividend M(x) :  ");
        String originalDividend = sc.next();

        //M'(x)
        String appendedDividend = originalDividend + "0".repeat(Math.max(0, divisorLength - 1));
        String tempDividend = appendedDividend.substring(0,divisorLength);
        System.out.println("\nM'(x) : " + appendedDividend);

        String crcRemainder = generateCRCRemainder(divisor,appendedDividend,tempDividend);
        System.out.println("\nCRC remainder : " + crcRemainder);

        //P(x)
        crcRemainder  = new StringBuilder(crcRemainder).insert(0,"0".repeat(originalDividend.length()-1)).toString();
        transmittedBitStream = performXOR(appendedDividend,crcRemainder);
        System.out.println("P(x) sent by the sender : " + transmittedBitStream);
        receiverFunction(divisor);
    }

    private static String generateCRCRemainder(String divisor, String dividend, String tempDividend) {
        //XOR function
        String tempRemainder = performXOR(tempDividend,divisor);
        int i =0, offset = divisorLength;
        while(i<(dividend.length()-divisorLength)){
            tempDividend = tempRemainder.substring(1)+dividend.charAt(offset);
            if(tempRemainder.charAt(1)=='0'){
                tempRemainder = performXOR(tempDividend,"0".repeat(divisorLength));
            }else{
                tempRemainder = performXOR(tempDividend,divisor);
            }
            i++;
            offset++;

            System.out.println("Temp remainder : " + tempRemainder+ "  ||   Temp dividend : " + tempDividend);
        }
        return tempRemainder;
    }

    private static void receiverFunction(String divisor) {
        //P'(x)
        System.out.println("\nEnter a transmitted bit stream with appended CRC : ");
        Scanner sc = new Scanner(System.in);
        String receivedBitStream = sc.next();
        System.out.println("\nP'(x) received by the receiver : "+ receivedBitStream);

        String tempDividend = receivedBitStream.substring(0,divisorLength);
        String noErrorCRCRemainder = "0".repeat(divisorLength);
        receivedBitStream += "0".repeat(Math.max(0, divisorLength - 1));
        String crcRemainder = generateCRCRemainder(divisor,receivedBitStream,tempDividend);
        System.out.println("\nCRC remainder : "+ crcRemainder);

        if(crcRemainder.equals(noErrorCRCRemainder))
            System.out.println("--No error detected during transmission--");
        else
            System.out.println("!!--Error detected during transmission--!!" );
    }

    private static String performXOR(String tempDividend, String divisor) {
        StringBuilder xorValue = new StringBuilder();
        for (int i = 0; i < divisor.length(); i++)
        {
            if (tempDividend.charAt(i)== divisor.charAt(i))
                xorValue.append("0");
            else
                xorValue.append("1");
        }
        return xorValue.toString();
    }

}