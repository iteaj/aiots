package com.iteaj.code.gen.vue;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.iteaj.code.LowCodeProperties;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 前端vue模板zip压缩
 */
public class FreemarkerVueTemplateZipEngine extends FreemarkerTemplateEngine {

    private ZipOutputStream zip;
    private LowCodeProperties properties;
    private Configuration configuration;
    public FreemarkerVueTemplateZipEngine(OutputStream outputStream, LowCodeProperties properties) {
        this.properties = properties;
        this.zip = new ZipOutputStream(outputStream);
    }

    @Override
    public FreemarkerTemplateEngine init(ConfigBuilder configBuilder) {
        this.setConfigBuilder(configBuilder);
        configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setDefaultEncoding(ConstVal.UTF8);
        configuration.setClassForTemplateLoading(FreemarkerTemplateEngine.class, StringPool.SLASH);
        configBuilder.setInjectionConfig(new VueInjectionConfig(configBuilder, this.properties));
        return this;
    }

    @Override
    public AbstractTemplateEngine batchOutput() {
        super.batchOutput();
        try {
            this.zip.finish();
            this.zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        try (StringWriter sw = new StringWriter()) {
            Template template = configuration.getTemplate(templatePath);
            template.setOutputEncoding("UTF-8");
            template.process(objectMap, sw);

            //添加到zip
            zip.putNextEntry(new ZipEntry(outputFile));
            zip.write(sw.toString().getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }

        logger.debug("模板:" + templatePath + ";  文件:" + outputFile);
    }

}
