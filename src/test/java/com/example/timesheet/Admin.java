package com.example.timesheet;

import com.example.timesheet.controller.MainController;
import com.example.timesheet.model.GongSi;
import com.example.timesheet.model.XiangMu;
import com.example.timesheet.model.YongHu;
import com.example.timesheet.util.PPJson;
import com.example.timesheet.util.PPUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.xml.transform.OutputKeys;
import java.time.LocalDate;

@Slf4j
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Admin extends TimesheetApplicationTests {

    private static HttpHeaders headers;

    @Before
    public void login() {
        if (headers == null) {
            headers = new HttpHeaders();
            String setCookie = super.login("Admin", "1234");
            headers.add(HttpHeaders.COOKIE, setCookie);
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
    }

    @Test
    public void 新建用户_成功() {
        PPJson ppJson = new PPJson();
        ppJson.put("mingCheng", "gt1");

        HttpEntity<String> request = new HttpEntity<>(
                ppJson.toString(),
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/createGongSi", HttpMethod.POST, request, String.class);
        checkCode(response, PPOK);

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        GongSi gongSi = gongSiRepository.findOneByMingCheng("gt1");
        Assert.assertNotNull(gongSi);
    }

    @Test
    public void 新建用户_失败_重名() {
        PPJson ppJson = new PPJson();
        ppJson.put("mingCheng", "g1");

        HttpEntity<String> request = new HttpEntity<>(
                ppJson.toString(),
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/createGongSi", HttpMethod.POST, request, String.class);
        checkCode(response, PPDuplicateExceptionCode);
    }

    @Test
    public void 删除用户_成功() {
        YongHu yongHu = yongHuRepository.findOneByYongHuMing("y3");

        HttpEntity<String> request = new HttpEntity<>(
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/deleteYongHu/" + yongHu.getId(), HttpMethod.DELETE, request, String.class);
        checkCode(response, PPOK);

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        yongHu = yongHuRepository.findOneByYongHuMing("y3");
        Assert.assertNull(yongHu);
    }

    @Test
    public void 删除用户_失败_有分配到项目() {
        YongHu yongHu = yongHuRepository.findOneByYongHuMing("y2");

        HttpEntity<String> request = new HttpEntity<>(
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/deleteYongHu/" + yongHu.getId(), HttpMethod.DELETE, request, String.class);
        checkCode(response, PPReferencedExceptionCode);

        yongHu = yongHuRepository.findOneByYongHuMing("y2");
        Assert.assertNotNull(yongHu);
    }

    @Test
    public void 删除用户_失败_有工作记录() {
        YongHu yongHu = yongHuRepository.findOneByYongHuMing("y1");

        HttpEntity<String> request = new HttpEntity<>(
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/deleteYongHu/" + yongHu.getId(), HttpMethod.DELETE, request, String.class);
        checkCode(response, PPReferencedExceptionCode);

        yongHu = yongHuRepository.findOneByYongHuMing("y1");
        Assert.assertNotNull(yongHu);
    }

    @Test
    public void 设置指定用户密码_成功() {
        YongHu yongHu = yongHuRepository.findOneByYongHuMing("y1");

        PPJson ppJson = new PPJson();
        ppJson.put("yongHuId", yongHu.getId());
        ppJson.put("password", "5678");

        HttpEntity<String> request = new HttpEntity<>(
                ppJson.toString(),
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/setYongHuPassword", HttpMethod.POST, request, String.class);
        checkCode(response, PPOK);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "y1");
        map.add("password", "5678");

        HttpEntity<String> response1 = restTemplate.postForEntity("/login", map, String.class);
        Assert.assertEquals(true, response1.toString().contains("homepage"));
    }

    @Test
    public void 新建公司_成功() {
        PPJson ppJson = new PPJson();
        ppJson.put("mingCheng", "gt1");

        HttpEntity<String> request = new HttpEntity<>(
                ppJson.toString(),
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/createGongSi", HttpMethod.POST, request, String.class);
        checkCode(response, PPOK);

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        GongSi gongSi = gongSiRepository.findOneByMingCheng("gt1");
        Assert.assertNotNull(gongSi);
    }

    @Test
    public void 新建公司_失败_重名() {
        PPJson ppJson = new PPJson();
        ppJson.put("mingCheng", "g1");

        HttpEntity<String> request = new HttpEntity<>(
                ppJson.toString(),
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/createGongSi", HttpMethod.POST, request, String.class);
        checkCode(response, PPDuplicateExceptionCode);
    }

    @Test
    public void 删除公司_成功() {
        GongSi gongSi = gongSiRepository.findOneByMingCheng("g3");

        HttpEntity<String> request = new HttpEntity<>(
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/deleteGongSi/" + gongSi.getId(), HttpMethod.DELETE, request, String.class);
        checkCode(response, PPOK);

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        gongSi = gongSiRepository.findOneByMingCheng("g3");
        Assert.assertNull(gongSi);
    }

    @Test
    public void 删除公司_失败_有项目() {
        GongSi gongSi = gongSiRepository.findOneByMingCheng("g1");

        HttpEntity<String> request = new HttpEntity<>(
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/deleteGongSi/" + gongSi.getId(), HttpMethod.DELETE, request, String.class);
        checkCode(response, PPReferencedExceptionCode);

        gongSi = gongSiRepository.findOneByMingCheng("g1");
        Assert.assertNotNull(gongSi);
    }

    @Test
    public void 设置公司名称_成功() {
        GongSi gongSi = gongSiRepository.findOneByMingCheng("g1");

        PPJson ppJson = new PPJson();
        ppJson.put("id", gongSi.getId());
        ppJson.put("mingCheng", "g1c");

        HttpEntity<String> request = new HttpEntity<>(
                ppJson.toString(),
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/setGongSiMingCheng", HttpMethod.POST, request, String.class);
        checkCode(response, PPOK);

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        gongSi = gongSiRepository.findOneByMingCheng("g1c");
        Assert.assertNotNull(gongSi);
    }

    @Test
    public void 设置公司名称_失败_重名() {
        GongSi gongSi = gongSiRepository.findOneByMingCheng("g1");

        PPJson ppJson = new PPJson();
        ppJson.put("id", gongSi.getId());
        ppJson.put("mingCheng", "g2");

        HttpEntity<String> request = new HttpEntity<>(
                ppJson.toString(),
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/setGongSiMingCheng", HttpMethod.POST, request, String.class);
        checkCode(response, PPDuplicateExceptionCode);
    }

    @Test
    public void 设置公司结算日_成功() {
        GongSi gongSi = gongSiRepository.findOneByMingCheng("g1");

        PPJson ppJson = new PPJson();
        ppJson.put("id", gongSi.getId());
        ppJson.put("jieSuanRi", "2000-01-01");

        HttpEntity<String> request = new HttpEntity<>(
                ppJson.toString(),
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/setGongSiJieSuanRi", HttpMethod.POST, request, String.class);
        checkCode(response, PPOK);

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        gongSi = gongSiRepository.findOneByMingCheng("g1");
        Assert.assertEquals(true, gongSi.getJieSuanRi().isEqual(LocalDate.of(2000, 1, 1)));
    }

    @Test
    public void 设置公司结算日_失败_日期不合法() {
        GongSi gongSi = gongSiRepository.findOneByMingCheng("g1");

        PPJson ppJson = new PPJson();
        ppJson.put("id", gongSi.getId());
        ppJson.put("jieSuanRi", "2000-02-31");

        HttpEntity<String> request = new HttpEntity<>(
                ppJson.toString(),
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/setGongSiJieSuanRi", HttpMethod.POST, request, String.class);
        checkCode(response, PPValidateExceptionCode);
    }

    @Test
    public void 新建项目_成功() {
        GongSi gongSi = gongSiRepository.findOneByMingCheng("g1");

        PPJson ppJson = new PPJson();
        ppJson.put("mingCheng", "g1x3");
        ppJson.put("gongSiId", gongSi.getId());

        HttpEntity<String> request = new HttpEntity<>(
                ppJson.toString(),
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/createXiangMu", HttpMethod.POST, request, String.class);
        checkCode(response, PPOK);

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        XiangMu xiangMu = xiangMuRepository.findOneByMingCheng("g1x3");
        Assert.assertNotNull(xiangMu);
        Assert.assertEquals(gongSi.getId(), xiangMu.getGongSi().getId());
    }

    @Test
    public void 新建项目_失败_重名() {
        GongSi gongSi = gongSiRepository.findOneByMingCheng("g1");

        PPJson ppJson = new PPJson();
        ppJson.put("mingCheng", "g1x1");
        ppJson.put("gongSiId", gongSi.getId());

        HttpEntity<String> request = new HttpEntity<>(
                ppJson.toString(),
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/createXiangMu", HttpMethod.POST, request, String.class);
        checkCode(response, PPDuplicateExceptionCode);
    }

    @Test
    public void 删除项目_成功_无工作记录_有计费标准() {
        XiangMu xiangMu = xiangMuRepository.findOneByMingCheng("g1x2");

        HttpEntity<String> request = new HttpEntity<>(
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/deleteXiangMu/" + xiangMu.getId(), HttpMethod.DELETE, request, String.class);
        checkCode(response, PPOK);

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        xiangMu = xiangMuRepository.findOneByMingCheng("g1x2");
        Assert.assertNull(xiangMu);
    }

    @Test
    public void 删除项目_失败_有工作记录() {
        XiangMu xiangMu = xiangMuRepository.findOneByMingCheng("g1x1");

        HttpEntity<String> request = new HttpEntity<>(
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange("/admin/deleteXiangMu/" + xiangMu.getId(), HttpMethod.DELETE, request, String.class);
        checkCode(response, PPReferencedExceptionCode);

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        xiangMu = xiangMuRepository.findOneByMingCheng("g1x1");
        Assert.assertNotNull(xiangMu);
    }
}