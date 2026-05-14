import dayjs from "dayjs";
import {theme} from 'antdv-next'
import {computed, reactive, ref} from "vue";
import useIllustrationTheme from "@/theme/illustrationTheme.js";
import {CompressOutlined, MoonOutlined, SunOutlined, SyncOutlined,} from '@antdv-next/icons'
import useCartoonTheme from "@/theme/cartoonTheme.js";

const themeClassPrefix = "iv-theme-";
const otherThemeMaps = {}

export function IVTheme(themeType, themeName) {
    this.themeType = themeType;
    this.themeName = themeName;

    this.getTheme = () => {
        switch (themeType) {
            case 'auto': {
                let current = dayjs().hour();
                return current >= 6&& current <18 ? 'light' : 'dark';
            }

            case 'light':
            case 'dark': return themeType;
            default: return 'light';
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

            default: return theme.defaultAlgorithm;
        }
    };

    this.getThemeConfig = () => {
        return otherThemeMaps[themeType];
    };
}
// 默认样式
const defaultStyle = 'auto';
// 纯白主题颜色配置
const light = new IVTheme( "light", "浅色主题");
const dark = new IVTheme("dark", "暗黑主题");
const compact = new IVTheme("compact", "紧凑主题");
const auto = new IVTheme("auto", "跟随系统");
const cartoon = new IVTheme("cartoon", "卡通风格");
const illustration = new IVTheme("illustration", "插画风格");
/**
 * @type {Ref<UnwrapRef<IVTheme[]>, UnwrapRef<IVTheme[]> | *[]>}
 */
let selectThemes = ref(null);
const themeMaps = {light, dark, compact, auto, cartoon, illustration}

export const themeMenus = [
    {key: auto.themeType, label: auto.themeName, icon: SyncOutlined},
    {key: light.themeType, label: light.themeName, icon: SunOutlined},
    {key: dark.themeType, label: dark.themeName, icon: MoonOutlined},
    {type: 'divider'},
    {key: cartoon.themeType, label: cartoon.themeName, icon: SunOutlined},
    {key: illustration.themeType, label: illustration.themeName, icon: SunOutlined},
    {type: 'divider'},
    {key: compact.themeType, label: compact.themeName, icon: CompressOutlined},
]

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

        // 主题切换的实际逻辑
        const updateTheme = () => {
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

            selectThemes.value = this.themeType.map(item => themeMaps[item]);
            this.switchColorPrimary(this.colorPrimary);
        };

        // 使用 View Transitions API 实现主题切换动效
        if (document.startViewTransition) {
            document.startViewTransition(() => updateTheme());
        } else {
            updateTheme();
        }
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
        selectThemes.value = this.themeType.map(item => themeMaps[item]);
    }

    this.loadingConfigFormStorage();

}
export {useCartoonTheme, useIllustrationTheme, illustration, cartoon}
const themeConfig = reactive(new IVThemeConfig());

/**
 * @param {IVTheme} theme
 * @param {Object} themeStyleConfig
 */
export const registerThemeConfig = (theme, themeStyleConfig) => {
    otherThemeMaps[theme.themeType] = themeStyleConfig;
    // 切换样式
    selectThemes.value = themeConfig.themeType.map(item => themeMaps[item]);
}

export const useIVTheme = () => {
    return computed(() => {
        let theme = selectThemes.value.find(item => item.getThemeConfig());
        let algorithm = selectThemes.value.map(item => item.getAlgorithm());

        let themeConfig = theme != null ? theme.getThemeConfig() : {};
        return {algorithm, ...themeConfig}
    })
}

export default themeConfig;
