package com.web.servlet;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class BaseServlet extends HttpServlet {
    private static final  long serialVersionUID=1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        super.service(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }


    //TODO:重要！！！！！！！！！！�?
    protected <T> T parseMap (HttpServletRequest request,Class<T> c) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T t=c.newInstance();

        //1.从request 取出map
        Map<String,String[]> map=request.getParameterMap();
        //entry条目的set集合
        Set<Map.Entry<String,String[]>> set=map.entrySet();

        Iterator<Map.Entry<String, String[]>> ite=set.iterator();
        //取出c的方�?
        Method[] ms=c.getMethods();
        //2.利用反射来创建t
        while (ite.hasNext()){
            Map.Entry<String, String[]> entry=ite.next();
            String key=entry.getKey();
            String[] values=entry.getValue();
            String value=null;

            if (values.length!=1){
                continue;
            }
            value=values[0];
            System.out.println("传�?�的参数:" +key+",值为"+value);
            Method m=findMethod(ms,key);
            if (m==null){
                continue;
            }
            System.out.println("找到要激活的方法:" +m.getName());
            String typeName=m.getParameterTypes()[0].getName();
            System.out.println(typeName);

            if ("java.lang.Integer".equals(typeName)  ||"int".equals(typeName)) {
                m.invoke(t, Integer.parseInt(value));
            } else if ("java.lang.Double".equals(typeName) ||"double".equals(typeName)) {
                m.invoke(t, Double.parseDouble(value));
            } else if ("java.lang.Float".equals(typeName)|| "float".equals(typeName)) {
                m.invoke(t, Float.parseFloat(value));
            } else if ("java.lang.Long".equals(typeName)|| "long".equals(typeName)) {
                m.invoke(t, Long.parseLong(value));
            } else if ("java.math.BigDecimal".equals(typeName)|| "BigDecimal".equals(typeName)) {
                m.invoke(t, new BigDecimal(value));
            } else {
                m.invoke(t, values);
            }
        }
        return t;

    }

    private Method findMethod(Method[] ms, String key) {
        for (Method m:ms) {
            String methodName="set"+key;
            if (m.getName().equalsIgnoreCase(methodName)){
                return m;
            }
        }
        return null;
    }


    protected void writeJson(Object obj,HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out=response.getWriter();
        Gson gson=new Gson();
        String s=gson.toJson(obj);
        out.println(s);
        out.flush();
        out.close();
    }
//    protected void go500(HttpServletRequest request,HttpServletResponse response,Exception e) throws ServletException, IOException {
//        request.setAttribute("errorMsg",e.getMessage() );
//        request.getRequestDispatcher("500.jsp").forward(request,response);
//    }
    protected void go404(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("404.jsp");
    }


}
