package com.iteaj.msn.dev.gen;

import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.IFileCreate;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.iteaj.framework.gen.GenTableInfo;
import com.iteaj.msn.dev.config.DevelopProperties;

import java.util.ArrayList;
import java.util.Map;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
public class GenInjectionConfig extends InjectionConfig {

    public static String H5_PATH = "h5_path";
    private DevelopProperties.CodeGen codeGen;

    public GenInjectionConfig(DevelopProperties.CodeGen codeGen) {
        this.codeGen = codeGen;
        this.setFileOutConfigList(new ArrayList<>());
    }

    @Override
    public void initMap() {
        this.getFileOutConfigList().add(new H5FileOut());
    }

    @Override
    public IFileCreate getFileCreate() {
        return null;
    }

    public class H5FileOut extends FileOutConfig {
        @Override
        public String outputFile(TableInfo tableInfo) {
            Map<String, String> pathInfo = getConfig().getPathInfo();
            GenTableInfo genTableInfo = (GenTableInfo) tableInfo;
            return String.format("%s/%s.html", pathInfo.get(H5_PATH)
                    , genTableInfo.getLowerFirstEntity());
        }

        @Override
        public String getTemplatePath() {
            return codeGen.getH5Template()+".vm";
        }

    }
}
