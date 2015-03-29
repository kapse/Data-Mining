package crime;

import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import org.apache.lucene.search.spell.JaroWinklerDistance;
import java.util.Vector;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

public class CommunalDetection 
{
   String appId;
    ArrayList AppList = new ArrayList();
    double Tsim = 0.8;
    double Tatt = 3;
    DetectFrame df;

    CommunalDetection(DetectFrame de, String id) {
        appId = id;
        df = de;
    }

    public void detect() {
        String currentApp = "";
        try {
            DBConnection db = new DBConnection();
            Statement st = db.stt;
            ResultSet rs = st.executeQuery("select * from Application where AppId!='" + appId + "'");
            while (rs.next()) {
                String a1 = rs.getString(1);
                String a2 = rs.getString(2);
                String a3 = rs.getString(3);
                String a4 = rs.getString(4);
                String a5 = rs.getString(5);
                String a6 = rs.getString(6);
                String a7 = rs.getString(7);
                String a8 = rs.getString(8);
                String a9 = rs.getString(9);
                String a10 = rs.getString(10);
                String a11 = rs.getString(11);
                String a12 = rs.getString(12);
                String a13 = rs.getString(13);
                String a14 = rs.getString(14);
                String a15 = rs.getString(15);
                String a16 = rs.getString(16);
                String a17 = rs.getString(17);
                String a18 = rs.getString(18);
              
                AppList.add(a1 + "#" + a2 + "#" + a3 + "#" + a4 + "#" + a5 + "#" + a6 + "#" + a7 + "#" + a8 + "#" + a9 + "#" + a10+ "#" + a11 + "#" + a12 + "#" + a13 + "#" + a14 + "#" + a15 + "#" + a16 + "#" + a17+ "#" + a18);
            }

            ResultSet rs1 = st.executeQuery("select * from Application where AppId='" + appId + "'");
            if (rs1.next()) {
                String a1 = rs1.getString(1);
                String a2 = rs1.getString(2);
                String a3 = rs1.getString(3);
                String a4 = rs1.getString(4);
                String a5 = rs1.getString(5);
                String a6 = rs1.getString(6);
                String a7 = rs1.getString(7);
                String a8 = rs1.getString(8);
                String a9 = rs1.getString(9);
                String a10 = rs1.getString(10);
                String a11 = rs1.getString(11);
                String a12 = rs1.getString(12);
                String a13 = rs1.getString(13);
                String a14 = rs1.getString(14);
                String a15 = rs1.getString(15);
                String a16 = rs1.getString(16);
                String a17 = rs1.getString(17);
                String a18 = rs1.getString(18);
                
                currentApp = a1 + "#" + a2 + "#" + a3 + "#" + a4 + "#" + a5 + "#" + a6 + "#" + a7 + "#" + a8 + "#" + a9 + "#" + a10+ "#" + a11 + "#" + a12 + "#" + a13 + "#" + a14 + "#" + a15 + "#" + a16 + "#" + a17+ "#" + a18 ;
            }

            String cr[] = currentApp.split("#");
            ArrayList mLink = new ArrayList();
            ArrayList RLink = new ArrayList();

            for (int i = 0; i < AppList.size(); i++) {
                String g1[] = AppList.get(i).toString().split("#");
                String ek1 = "";
                int ek2 = 0;
                for (int j = 1; j < g1.length; j++) {
                    JaroWinklerDistance jd = new JaroWinklerDistance();
                    double at = jd.getDistance(cr[j], g1[j]);
                    
                    if (at >= Tsim) {
                        ek1 = ek1 + "1";
                        ek2++;
                    } else {
                        ek1 = ek1 + "0";
                       
                    }
                }
                
                System.out.println(g1[0] + " : " + ek1 + " : " + ek2);

                if (Tatt <= ek2 && ek2 <= g1.length - 1) {
                    mLink.add(ek1);
                    if (!RLink.contains(ek1)) {
                        RLink.add(ek1);
                    }
                } else {
                    mLink.add("-");
                }
            }
            System.out.println("mLink " + mLink);
            ArrayList rc = new ArrayList();
            for (int i = 0; i < RLink.size(); i++) {
                String g1 = RLink.get(i).toString();
                int k = 0;
                for (int j = 0; j < mLink.size(); j++) {
                    String g2 = mLink.get(j).toString();
                    if (g1.equals(g2)) {
                        k++;
                    }
                }
                rc.add(k);
            }
            System.out.println("RLink " + RLink);
            System.out.println("Rc " + rc);
            Object ob1[] = RLink.toArray();
            Object ob2[] = rc.toArray();

            for (int i = 0; i < ob2.length; i++) {
                for (int j = 0; j > ob2.length; j++) {
                    if (Integer.parseInt(ob2[i].toString()) < Integer.parseInt(ob2[j].toString())) {
                        Object t1 = ob1[i];
                        ob1[i] = ob1[j];
                        ob1[j] = t1;
                        Object t2 = ob2[i];
                        ob2[i] = ob2[j];
                        ob2[j] = t2;

                    }
                }
            }
            DefaultTableModel dm = (DefaultTableModel) df.jTable1.getModel();
            
            for (int i = 0; i < ob1.length; i++) {
                
                Vector v = new Vector();
                v.add(i + 1);
                v.add(ob1[i]);
                v.add(ob2[i]);
                v.add((double) (i + 1) / 4);
                dm.addRow(v);
            }
                 

            double scr1 = 0;
            double a = 0.8;
            for (int i = 0; i < ob1.length; i++) {
                scr1 = scr1 + ((1 - a) * Double.parseDouble(ob2[i].toString()));
            }
            System.out.println("scr1 " + scr1);
            DecimalFormat dft = new DecimalFormat("#.##");
            String d = dft.format(scr1);
            double sus = Double.parseDouble(d);
            JOptionPane.showMessageDialog(df, "suspicion score " + sus);

             if (sus >= 0.6) {
//                st.executeUpdate("update Status set Status='Checked',Verification='Rejected' where AppId='" + appId + "'");
                JOptionPane.showMessageDialog(df, "Application " + appId + " is Rejected");
            } else {
//                st.executeUpdate("update Status set Status='Checked',Verification='Accepted' where AppId='" + appId + "'");
                JOptionPane.showMessageDialog(df, "Application " + appId + " is Accepted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
