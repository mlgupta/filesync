package dbsentry.filesync.common;

import java.io.Serializable;

import java.util.Date;


/**
 * Constructs a packet which could be used to transfer data between client and server.Packet holds 
 * enough information so that all the packets can be reassembled at other side.
 * @author             Jeetendra Prasad.
 * @version            1.0
 * Date of creation:   08-05-2005
 * Last Modfied by :    
 * Last Modfied Date: 
 */
public class PacketCarrier implements Serializable {
  private int totalLength;

  private byte data[];

  private int offset;

  private int length;

  private int totalPacket;

  private String carrierCode;

  private int packetNumber;

  private Date sentDate;

  /**
   * Sets the totalLength which is the length of the total data of which this packet is a part.
   * @param totalLength total length of data to be sent in integer.
   */
  public void setTotalLength(int totalLength) {
    this.totalLength = totalLength;
  }


  /**
   * Retrieves the totalLength value.
   * @return integer totalLength.
   */
  public int getTotalLength() {
    return totalLength;
  }


  /**
   * Sets the data field of this packet to specified byte array.
   * @param data byte array to be set as data.
   */
  public void setData(byte[] data) {
    this.data = data;
  }


  /**
   * Retrieves the data field of this packet.
   * @return byte array containing data.
   */
  public byte[] getData() {
    return data;
  }


  /**
   * Sets the offset value to the offset of data which is being carried in this packet.
   * @param offset integer containing offset value.
   */
  public void setOffset(int offset) {
    this.offset = offset;
  }


  /**
   * Retrieves the offset field of this packet.
   * @return integer offset.
   */
  public int getOffset() {
    return offset;
  }


  /**
   * Sets the length to the length of this packet.
   * @param length integer length.
   */
  public void setLength(int length) {
    this.length = length;
  }


  /**
   * Retrieves the length of this packet.
   * @return integer packet length.
   */
  public int getLength() {
    return length;
  }


  /**
   * Sets the no. of  packets in the series of which this packet is a part.
   * @param totalPacket no. of packets.
   */
  public void setTotalPacket(int totalPacket) {
    this.totalPacket = totalPacket;
  }


  /**
   * Gives the no.of packets in the series of which this packet is a part.
   * @return  totalPacket integer no. of packets.
   */
  public int getTotalPacket() {
    return totalPacket;
  }


  /**
   * Sets the carrierCode to the specified String.
   * @param carrierCode String tobe set as carrierCode.
   */
  public void setCarrierCode(String carrierCode) {
    this.carrierCode = carrierCode;
  }


  /**
   * Returns the carrierCode stored in this packet.
   * @return String carrierCode.
   */
  public String getCarrierCode() {
    return carrierCode;
  }


  /**
   * Sets the packetNumber of this packet.
   * @param packetNumber integer packet number.
   */
  public void setPacketNumber(int packetNumber) {
    this.packetNumber = packetNumber;
  }


  /**
   * Gives the packetNumber of this packet.
   * @return integer packetNumber.
   */
  public int getPacketNumber() {
    return packetNumber;
  }

  /**
   * gives String representation of PacketCarrier object.
   * @return String repersentation of PacketCarrier object.
   */
  public String toString() {
    String strTemp = "";
    strTemp += "\n\t Total Length : " + totalLength;
    strTemp += "\n\t Offset : " + offset;
    strTemp += "\n\t Packet length : " + length;
    strTemp += "\n\t No. of packets : " + totalPacket;
    strTemp += "\n\t Carrier Code : " + carrierCode;
    strTemp += "\n\t Packet Number : " + packetNumber;

    return strTemp;
  }

  /**
   * 
   * @return 
   */
  public Date getSentDate() {
    return sentDate;
  }

  /**
   * 
   * @param sentDate
   */
  public void setSentDate(Date sentDate) {
    this.sentDate = sentDate;
  }

}
