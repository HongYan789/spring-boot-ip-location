package com.hongyan.study.springbootiplocation;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.paraview.common.geolocation.IpLocationService;
import com.paraview.common.geolocation.Location;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import javax.annotation.Resource;
import java.io.File;
import java.util.Objects;

@SpringBootTest
@Slf4j
class SpringBootIpLocationApplicationTests {

    @Resource
    private IpLocationService ipLocationService;

    /**
     *  利用ip获取地理位置：para内部封装版本
     */
    @Test
    void getIpLocationTestMethod1() {
//        String data = "155.33.44.55";
        String data = "27.16.150.186";
        log.info("location: {}", getLocation(data));
    }


    /**
     * 获取地理位置
     * @param data
     * @return
     */
    private String getLocation(String data) {
        if (StringUtils.isEmpty(data)) {
            return "-";
        }
        try {
            Location location = ipLocationService.getLocationExternel(data);
            if (location != null) {
                // 去掉市字 例如：北京市 -> 北京
                // 拼音转换 例如：北京 -> beijing
                // 拼音首字母大写 例如：beijing -> Beijing
                return StringUtils.capitalize(PinyinUtil.getPinyin(StringUtils.remove(location.getCity(), "市"), ""));
            }
        } catch (Exception e) {
            log.error("获取地理位置失败", e);
            return "-";
        }
        return "-";
    }


    /**
     * 利用ip获取地理位置：ip2region版本
     * https://juejin.cn/post/7116817252195762213
     * https://github.com/lionsoul2014/ip2region
     */
    @Test
    void getIpLocationTestMethod2() {
        String data = "155.33.44.55";
//        String data = "27.16.150.186";
        log.info("location: {}", getIp2Location(data));
    }

    private static final String TEMP_FILE_DIR = System.getProperty("user.dir");

    /**
     * https://juejin.cn/post/7116817252195762213
     * @param ip
     * @return
     */
    private String getIp2Location(String ip) {
        try {
            // 获取当前记录地址位置的文件
            //q: 如何获取项目resources目录
            String dbPath = Objects.requireNonNull(new ClassPathResource("ip2region/ip2region.xdb")).getPath();
            File file = new File(dbPath);
            //如果当前文件不存在，则从缓存中复制一份
            if (!file.exists()) {
//                dbPath = Objects.requireNonNull(new ClassPathResource("ip2region/ip.db")).getPath();
                dbPath = TEMP_FILE_DIR + "/ip.db";
                log.info("当前目录为:[{}]", dbPath);
                file = new File(dbPath);
                FileUtils.copyInputStreamToFile(Objects.requireNonNull(new ClassPathResource("ip2region/ip2region.xdb").getInputStream()), file);
            }
            //创建查询对象
            Searcher searcher = Searcher.newWithFileOnly(dbPath);
            //开始查询
            return searcher.search(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //默认返回空字符串
        return "";
    }

}
