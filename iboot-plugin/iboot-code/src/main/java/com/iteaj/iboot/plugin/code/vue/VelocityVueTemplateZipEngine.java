package com.iteaj.iboot.plugin.code.vue;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.iteaj.iboot.plugin.code.LowCodeProperties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 前端vue模板zip压缩
 */
public class VelocityVueTemplateZipEngine extends VelocityTemplateEngine {

    private ZipOutputStream zip;
    private VelocityEngine velocityEngine;
    private LowCodeProperties properties;
    public VelocityVueTemplateZipEngine(OutputStream outputStream, LowCodeProperties properties) {
        this.properties = properties;
        this.zip = new ZipOutputStream(outputStream);
    }

    @Override
    public VelocityTemplateEngine init(ConfigBuilder configBuilder) {
        if (null == velocityEngine) {
            Properties p = new Properties();
            p.setProperty(ConstVal.VM_LOAD_PATH_KEY, ConstVal.VM_LOAD_PATH_VALUE);
            p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, StringPool.EMPTY);
            p.setProperty(Velocity.ENCODING_DEFAULT, ConstVal.UTF8);
            p.setProperty(Velocity.INPUT_ENCODING, ConstVal.UTF8);
            p.setProperty("file.resource.loader.unicode", StringPool.TRUE);
            velocityEngine = new VelocityEngine(p);
        }

        configBuilder.setInjectionConfig(new VueInjectionConfig(this.properties, configBuilder));
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
    public void writer(Map<String, Object> objectMap, String templatePath, File outputFile) throws Exception {
        Template template = velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
        try (StringWriter sw = new StringWriter()) {
            template.merge(new VelocityContext(objectMap), sw);

            //添加到zip
            zip.putNextEntry(new ZipEntry(outputFile.getPath()));
            zip.write(sw.toString().getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
    }

    protected VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }
}
