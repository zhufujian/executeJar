package com.study.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;


/**
 * 
* @Company 连连银通电子支付有限公司
*
* @Description: 批量签约
* @ClassName: BankSigndeForVersion1_2_2 
* @author zhufj
* @date 2018年5月23日 上午11:15:12 
*
 */
public class ExecuteDemo {
	protected static Logger log = Logger.getLogger(ExecuteDemo.class);


	/*
	 * 商户名称+支付方式+认证类型+处理标识1+处理标识2
	 * java -jar ../executeJar.jar D:/20190228.txt D:/20190228_out.txt D:/20190228_out_error.txt &
	 */

	public static void main(String[] args) {
		log.info("请求参数["+JSON.toJSONString(args)+"]");
		if (args == null || args.length < 3) {
			log.info("请求参数["+JSON.toJSONString(args)+"]非法");
			return; 
		}
		BufferedReader br = null;
		BufferedWriter bw = null;
		BufferedWriter bw_error = null;
		//ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dubbo-consumer.xml");
		//context.start();
		//demoService = (IDemoService) context.getBean("demoService");
		String correlationID = "";
		try {
			log.info("开始读取数据");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					args[0]), "UTF-8"));
			bw = new BufferedWriter(new FileWriter(args[1]));
			bw_error = new BufferedWriter(new FileWriter(args[2]));
			String line = "";
			ResulltInfo resulltInfo = null;
			int n = 0;
			long start = System.currentTimeMillis();
			while ((line = br.readLine()) != null) {
				n++;
				correlationID = UUID.randomUUID().toString();
				log.info("correlationID[" + correlationID + "]第[" + n + "]行["
						+ line + "]");
				if (isnull(line)) {
					log.info("correlationID[" + correlationID + "]数据为空");
					bw_error.write(line + ",,数据为空,");
					bw_error.newLine();
					bw_error.flush();
					continue;
				}
				resulltInfo = bankSigned(line, correlationID);
				if (!resulltInfo.isSuccess()) {
					bw_error.write(resulltInfo.getResultMsg());
					bw_error.newLine();
					bw_error.flush();
					continue;
				}
				bw.write(resulltInfo.getResultMsg());
				bw.newLine();
				bw.flush();
			}
			long end = System.currentTimeMillis();
			log.info("correlationID[" + correlationID + "]迁移完成耗时["
					+ (end - start) + "]");
		} catch (Exception e) {
			log.error(
					"correlationID[" + correlationID + "]系统异常:"
							+ e.getMessage(), e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			if (bw != null) {
				try {
					bw.flush();
					bw.close();
				} catch (IOException e) {
					// no active
				}
			}
			if (bw_error != null) {
				try {
					bw_error.flush();
					bw_error.close();
				} catch (IOException e) {
					// no active
				}
			}
		}
	}

	private static ResulltInfo bankSigned(String line, String correlationID) {
		ResulltInfo resulltInfo = new ResulltInfo();
			// 获取序列号
				resulltInfo.setSuccess(true);
				resulltInfo.setResultMsg(line + ",,执行完成,");
			return resulltInfo;
	}
	private static boolean isnull(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}
}
