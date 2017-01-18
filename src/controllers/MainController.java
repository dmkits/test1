package controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * Created by ianagez on 16.01.17.
 */
public class MainController extends PageController {

    public MainController() { sPAGE_URL="/pages/main.html"; }

    @Override
    protected boolean doGetAction(String sAction, HttpServletRequest req, HttpSession session, HashMap outData) {
        if("get_data".equals(sAction)){
        } else return false;
        return true;
    }

    @Override
    protected boolean doPostAction(String sAction, HttpServletRequest req, HttpSession session, HashMap data) throws Exception {
        if ("exit".equals(sAction)) {
            session.invalidate();
            data.put("actionResult","successfull");
            return true;
        } else if ("load_file".equals(sAction)) {
            return true;
        } else return super.doPostAction(sAction, req, session, data);
//        return true;
    }

}
