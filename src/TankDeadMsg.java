import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class TankDeadMsg implements Msg {
	int msgType = Msg.TANK_DEAD_MSG;
	TankClient tc;
	int id;
	
	public TankDeadMsg(int id) {
		this.id = id;
	}
	
	public TankDeadMsg(TankClient tc) {
		this.tc = tc;
	}
	
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();    //默认32字节
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(id);
		} catch (IOException e) {
			e.printStackTrace();
		}  
		byte[] buf = baos.toByteArray();
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, udpPort));
			ds.send(dp);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();                 //与上面发送的信息一一对应
			/**
			 * 如果坦克和自己坦克id一致则返回，不一致则加入到tc中将其画出
			 */
			if(tc.myTank.id == id) {
				return;
			}
			for(int i=0; i<tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if(t.id == id) {
					t.setLive(false);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
