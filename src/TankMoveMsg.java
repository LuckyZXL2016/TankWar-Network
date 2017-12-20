import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class TankMoveMsg implements Msg {
	int msgType = Msg.TANK_MOVE_MSG;
	int x,y;
	int id;
	Dir ptDir;
	Dir dir;
	TankClient tc;
	
	
	public TankMoveMsg(int id, int x, int y, Dir dir, Dir ptDir) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.ptDir = ptDir;
	}

	public TankMoveMsg(TankClient tc) {
		this.tc = tc;
	}
	
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();    //默认32字节
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(id);
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(dir.ordinal());        //ordinal 指的就是枚举类型的下标值
			dos.writeInt(ptDir.ordinal());
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
			int x = dis.readInt();
			int y = dis.readInt();
			Dir dir = Dir.values()[dis.readInt()];
			Dir ptDir = Dir.values()[dis.readInt()];
			boolean exist = false;
			for(int i=0; i<tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if(t.id == id) {
					t.x = x;
					t.y = y;
					t.dir = dir;
					t.ptDir = ptDir;
					exist = true;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
