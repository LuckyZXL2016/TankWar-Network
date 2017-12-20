import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class TankNewMsg implements Msg {
	int msgType = Msg.TANK_NEW_MSG;
	
	Tank tank;
	TankClient tc;
	public TankNewMsg(Tank tank) {
		this.tank = tank;
	}
	
	public TankNewMsg(TankClient tc) {
		this.tc = tc;
	}
	
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();    //默认32字节
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(tank.id);
			dos.writeInt(tank.x);
			dos.writeInt(tank.y);
			dos.writeInt(tank.dir.ordinal());        //ordinal 指的就是枚举类型的下标值
			dos.writeBoolean(tank.good);
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
			boolean good = dis.readBoolean();
//System.out.println("id:" + id + "-x:" + x + "-y:" + y + "-dir:" + dir + "-good:" + good);
			boolean exist = false;
			for(int i=0; i<tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if(t.id == id) {
					exist = true;
					break;
				}
			}
			if(!exist) {
				TankNewMsg tnMsg = new TankNewMsg(tc.myTank);
				tc.nc.send(tnMsg);
				
				Tank t = new Tank(x, y, good, dir, tc);
				t.id = id;
				tc.tanks.add(t);
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
