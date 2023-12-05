package com.koreaIT.java.am;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.koreaIT.java.am.config.Config;
import com.koreaIT.java.am.util.DBUtil;
import com.koreaIT.java.am.util.SecSql;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/member/doJoin") // url매핑
public class MemberDoJoinServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html; charset=UTF-8;");
		
		String loginId = request.getParameter("loginId");
		String loginPw = request.getParameter("loginPw");
		String name = request.getParameter("name");
		
		Connection conn = null;

		try {
			Class.forName(Config.getDBDriverName());
			String url = Config.getDBUrl();
			conn = DriverManager.getConnection(url, Config.getDBUser(), Config.getDBPassWd());

			SecSql sql = new SecSql();
			sql.append("SELECT COUNT(*) FROM `member`");
			sql.append("WHERE loginId = ?", loginId);
			
			int loginIdDupChk = DBUtil.selectRowIntValue(conn, sql);
			
			if (loginIdDupChk == 1) {
				response.getWriter().append(String.format("<script>alert('%s은(는) 이미 사용중인 아이디입니다'); location.replace('join');</script>", loginId));
				return;
			}
			
			sql = new SecSql();
			sql.append("INSERT INTO `member`");
			sql.append("SET regDate = NOW(),");
			sql.append("updateDate = NOW(),");
			sql.append("loginId = ?,", loginId);
			sql.append("loginPw = ?,", loginPw);
			sql.append("name = ?", name);
			
			DBUtil.insert(conn, sql);
			
			response.getWriter().append(String.format("<script>alert('%s 회원님이 가입되었습니다'); location.replace('../home/main');</script>", name));
			
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("에러 : " + e);
		} finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}