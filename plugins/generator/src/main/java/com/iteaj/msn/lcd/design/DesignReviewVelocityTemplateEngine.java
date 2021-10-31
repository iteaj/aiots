package com.iteaj.msn.lcd.design;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.iteaj.msn.lcd.LowCodeProperties;
import com.iteaj.msn.lcd.dto.ReviewDto;
import com.iteaj.msn.lcd.gen.DesignConfigBuilder;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.StringWriter;
import java.util.Map;

public class DesignReviewVelocityTemplateEngine extends LcdVelocityTemplateEngine {

    private LowCodeProperties properties;
    private ReviewDto reviewDto = new ReviewDto();

    public DesignReviewVelocityTemplateEngine(LowCodeProperties properties) {
        this.properties = properties;
    }

    @Override
    public DesignReviewVelocityTemplateEngine init(DesignConfigBuilder configBuilder) {
        super.init(configBuilder);
        this.setConfigBuilder(configBuilder);
        configBuilder.setInjectionConfig(new LcdVueInjectionConfig(configBuilder, this.properties) {
            @Override
            public ILcdFileCreate getFileCreate() {
                return (configBuilder, fileType, filePath) -> true;
            }
        });

        return this;
    }

    @Override
    public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        Template template = this.getVelocityEngine().getTemplate(templatePath, ConstVal.UTF8);
        TableInfo tableInfo = (TableInfo) objectMap.get("table");
        try (StringWriter sw = new StringWriter()) {
            template.merge(new VelocityContext(objectMap), sw);
            if(outputFile.endsWith(".vue")) {
                this.reviewDto.setHtml(sw.toString());
            } else if(outputFile.contains(tableInfo.getXmlName())
                    && outputFile.endsWith(".xml")) {
                this.reviewDto.setMapperXml(sw.toString());
            } else if(outputFile.contains(tableInfo.getMapperName())
                    && outputFile.endsWith(".java")) {
                this.reviewDto.setMapper(sw.toString());
            } else if(outputFile.contains(tableInfo.getServiceName())) {
                this.reviewDto.setService(sw.toString());
            } else if(outputFile.contains(tableInfo.getServiceImplName())) {
                this.reviewDto.setServiceImpl(sw.toString());
            } else if(outputFile.contains(tableInfo.getControllerName())) {
                this.reviewDto.setController(sw.toString());
            } else if(outputFile.contains(tableInfo.getEntityName())) {
                this.reviewDto.setEntity(sw.toString());
            }
        }
    }

    public ReviewDto getReviewDto() {
        return reviewDto;
    }

    public void setReviewDto(ReviewDto reviewDto) {
        this.reviewDto = reviewDto;
    }
}
