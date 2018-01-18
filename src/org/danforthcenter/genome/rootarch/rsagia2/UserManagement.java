package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.jooq.DSLContext;

public class UserManagement {
    private ArrayList<String> groupList;
    private String user;

    public void UserManagement()
    {

    }

    public ArrayList<String> findUserGroups(String userName, String os)
    {
        this.user = userName;
        String command= null;
        if(os.contains("Windows"))
        {
            command = "net user "+ userName;

        }
        else if(os.contains("Linux"))
        {
            command = "id -Gn" + userName;
        }
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if(os.contains("Windows"))
            {
                this.groupList = findWindowsUserGroups(reader);
            }
            else if(os.contains("Linux"))
            {
                this.groupList = findLinuxUserGroups(reader);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return this.groupList;
    }

    public ArrayList<String> findWindowsUserGroups(BufferedReader reader) throws IOException {
        ArrayList<String> larray = new ArrayList<String>();
        String s;
        String[] arr = null;
        while ((s = reader.readLine()) != null) {
            if (s.contains("Local Group Memberships")) {
                arr = s.split("\\*");
                if (arr != null) {
                    for (String a : arr) {
                        if(!a.contains("Local Group Memberships")&&!a.trim().equals("")) {
                            larray.add(a.trim());
                        }
                    }
                }
            }
            try
            {
                if ((larray.size()>1) && !(s.contains("Global Group memberships")) && !s.contains("Local Group Memberships")) {
                    arr = s.split("\\*");
                    if(arr!=null) {
                        for (String a : arr) {
                            if(!a.trim().equals("")) {
                                larray.add(a.trim());
                            }
                        }
                    }
                }
                if (s.contains("Global Group memberships")) {
                    break;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
        return larray;
    }

    public ArrayList<String> findLinuxUserGroups(BufferedReader reader) throws IOException {
        ArrayList<String> larray = new ArrayList<String>();
        String s;
        String[] arr = null;
        while ((s = reader.readLine())!= null)
        {
            arr = s.split(" ");
            if(arr!=null) {
                for (String a : arr)
                {
                    if (!a.trim().equals(""))
                    {
                        larray.add(a.trim());
                    }
                }
            }
        }
        return larray;
    }
}
