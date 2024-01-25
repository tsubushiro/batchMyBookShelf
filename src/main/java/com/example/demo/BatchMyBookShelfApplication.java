package com.example.demo;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class BatchMyBookShelfApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchMyBookShelfApplication.class, args)
		.getBean(BatchMyBookShelfApplication.class).execute();
	}
	
	@Autowired
	JdbcTemplate jdbctemplate;
	
	// バックアップ処理した時間
	LocalDate backupDate;
	
	// テスト
//	private void test() {
//		String sql = "SELECT * from account";
//		List<Map<String,Object>> resultList = jdbctemplate.queryForList(sql);
//		if(resultList != null) {
//			for(Map<String,Object> result: resultList) {
//				String name = (String) result.get("name");
//				System.out.println(name);
//			}
//		}
//	}
	// 退会ユーザのデータ退避
	private void backupCancelUser() {
		int result = 0 ;
		String[] sqlList = {
				"insert into _bk_readRecord "
				+ " select *,? from readRecord"
				+ " where status=-1",
				"insert into _bk_readPlan "
				+ " select *,? from readplan"
				+ " where status=-1",
				"insert into _bk_memoComment"
				+ " select *,? from memoComment"
				+ " where status=-1",
				"insert into _bk_bookshelf"
				+ " select *,? from bookshelf"
				+ " where status=-1",
				"insert into _bk_account"
				+ " select *,? from account"
				+ " where status=-1",
				};
		for(String sql:sqlList) {
			result = jdbctemplate.update(sql,Date.valueOf(backupDate));
			System.out.println(result+":"+sql);
		}
		
	}
	// 退会ユーザのデータの物理削除
	private void deleteCancelUser() {
		int result =0 ;
		String[] sqlList = {
				// 参照キーの対応で逆順
				"delete from readRecord where status=-1",
				"delete from readPlan where status=-1",
				"delete from memoComment where status=-1",
				"delete from bookShelf where status=-1",
				"delete from account where status=-1",
		};
		for(String sql:sqlList) {
			result = jdbctemplate.update(sql);
			System.out.println(result+":"+sql);
			// System.out.println(sql);
		}
	}

	// 実行
	private void execute() {
		backupDate =  LocalDate.now();
		System.out.println("バックアップ開始:"+backupDate);
		backupCancelUser();// 退会ユーザのデータのバックアップ
		deleteCancelUser();// 削除ユーザのデータの物理削除
		System.out.println("バックアップ終了:"+backupDate);
	}

}
