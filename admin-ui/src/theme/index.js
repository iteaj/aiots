import dayjs from "dayjs";
import { theme } from 'antdv-next'

const themeClassPrefix = "iv-theme-";
export function IVTheme(themeType) {
    this.theme = themeType;
    this.getTheme = () => {
        switch (themeType) {
            case 'auto': {
                let current = dayjs().hour();
                return current >= 6&& current <18 ? 'light' : 'dark';
            }
            default: return themeType;
        }
    }

    this.getAlgorithm = () => {
        switch (themeType) {
            case 'dark': return theme.darkAlgorithm;
            case 'light': return theme.defaultAlgorithm;
            case 'compact': return theme.compactAlgorithm;
            case 'auto': {
                let current = dayjs().hour();
                return current >= 6 && current <18 ? theme.defaultAlgorithm : theme.darkAlgorithm;
            }
        }
    };
}
// 默认样式
const defaultStyle = 'auto';
// 纯白主题颜色配置
const light = new IVTheme( "light");
const dark = new IVTheme("dark");
const compact = new IVTheme("compact");
const auto = new IVTheme("auto");
const themeMaps = {light, dark, compact, auto}
export function IVThemeConfig() {

    this.themeType = [defaultStyle]; // dark

    /**
     * 配置Drawer开关状态
     * @type {boolean}
     */
    this.open = false;

    /**
     * @type {IVTheme}
     */
    this.theme = light;

    this.algorithm = null;

    /**
     * 主颜色
     * @type {string}
     */
    this.colorPrimary = "#1677FF";

    /**
     * 候选主题颜色
     * @type {[{value: string}, {value: string}, {value: string}, {value: string}]}
     */
    this.candidateColorPrimary = [{value: '#1677FF'}, {value: '#00B96B'}, {value: '#722ED1'}, {value: '#FA541C'}];

    /**
     * 顶栏用主题色
     * @type {boolean}
     */
    this.headerColorPrimary = false;

    /**
     * 切换配置状态
     */
    this.switchOpen = function () {
        this.open = !this.open;
    }

    this.switchTheme = function (themeType) {
        let number = this.themeType.indexOf("compact");
        if(themeType == 'compact') {
            if(number > -1) {
                this.themeType.splice(number, 1);
            } else {
                this.themeType.push(themeType);
            }
        } else {
            this.themeType = [themeType]
            this.theme = themeMaps[themeType];
            if(number > -1) this.themeType.push("compact");
            document.documentElement.className = themeClassPrefix + this.theme.getTheme();
        }

        this.algorithm = this.themeType.map(item => themeMaps[item].getAlgorithm());
        this.switchColorPrimary(this.colorPrimary);
    }

    /**
     * 切换主颜色
     * @param value
     */
    this.switchColorPrimary = function (value) {
        this.colorPrimary = value;
        this.colorBackgroundPrimary = (value + "17");
        this.switchHeaderColorPrimary(this.headerColorPrimary);
    }

    /**
     * 头部使用主题色作为背景
     */
    this.switchHeaderColorPrimary = function (value) {
        this.headerColorPrimary = value;
        // 存储配置信息
        localStorage.setItem("localThemeConfig", JSON.stringify(this));
    }

    /**
     * 加载配置
     */
    this.loadingConfigFormStorage = function () {
        let localThemeConfig = localStorage.getItem("localThemeConfig");

        if(typeof localThemeConfig === 'string') {
            localThemeConfig = JSON.parse(localThemeConfig);
            this.themeType = localThemeConfig.themeType;
            this.colorPrimary = localThemeConfig.colorPrimary;
            this.headerColorPrimary = localThemeConfig.headerColorPrimary;
        } else {
            localStorage.setItem("localThemeConfig", JSON.stringify(this));
        }

        document.documentElement.className = themeClassPrefix + themeMaps[this.themeType[0]].getTheme();
        // 切换样式
        this.algorithm = this.themeType.map(item => themeMaps[item].getAlgorithm());
    }

    this.loadingConfigFormStorage();

}

export default new IVThemeConfig();
