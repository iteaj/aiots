// 卡通主题

import {theme} from "antdv-next";

/**
 * @param {CreateStylesReturn} useStyles
 */
function useCartoonTheme(useStyles) {
    const { styles } = useStyles

    return {
        cssVar: true,
        // algorithm: theme.defaultAlgorithm,
        modal: {
            classes: {
                container: styles.sharedBorder,
            },
        },
        colorPicker: {
            arrow: false,
        },
        popover: {
            arrow: false,
            classes: {
                root: styles.sharedBorder,
            },
        },
        progress: {
            classes: {
                rail: styles.sharedBorder,
            },
            styles: {
                rail: {
                    border: '2px solid #225555',
                    boxSizing: 'border-box',
                    height: '16px',
                },
                track: {
                    border: '2px solid #225555',
                    boxSizing: 'border-box',
                    height: '16px',
                    marginInlineStart: '-2px',
                    marginBlockStart: '-2px',
                },
            },
        },
        token: {
            colorText: '#51463B',
            colorPrimary: '#225555',
            colorError: 'red',
            colorInfo: '#9CD3D3',
            colorInfoBorder: '#225555',
            colorBorder: '#225555',
            colorBorderSecondary: '#225555',
            lineWidth: 2,
            lineWidthBold: 2,
            borderRadius: 18,
            borderRadiusLG: 18,
            borderRadiusSM: 18,
            controlHeightSM: 28,
            controlHeight: 36,
            colorBgBase: '#FAFAEE',
        },
        components: {
            Button: {
                primaryShadow: 'none',
                dangerShadow: 'none',
                defaultShadow: 'none',
                colorText: '#51463B',
                colorPrimary: '#225555',
                colorError: '#DA8787',
                colorInfo: '#9CD3D3',
                colorInfoBorder: '#225555',
                colorBorder: '#225555',
                colorBorderSecondary: '#225555',
            },
            Modal: {
                boxShadow: 'none',
            },
            Card: {
                colorBgContainer: '#BBAA99',
            },
            Tooltip: {
                borderRadius: 6,
                colorBorder: '#225555',
                algorithm: true,
            },
            Select: {
                optionSelectedBg: '#CBC4AF',
            }
        }
    }
}

export const themeConfig = {
    token: {
        colorText: '#51463B',
        colorPrimary: '#225555',
        colorError: '#DA8787',
        colorInfo: '#9CD3D3',
        colorInfoBorder: '#225555',
        colorBorder: '#225555',
        colorBorderSecondary: '#225555',
        lineWidth: 2,
        lineWidthBold: 2,
        borderRadius: 18,
        borderRadiusLG: 18,
        borderRadiusSM: 18,
        controlHeightSM: 28,
        controlHeight: 36,
        colorBgBase: '#FAFAEE',
    },
    components: {
        Button: {
            primaryShadow: 'none',
            dangerShadow: 'none',
            defaultShadow: 'none',
            colorText: '#51463B',
            colorPrimary: '#225555',
            colorError: '#DA8787',
            colorInfo: '#9CD3D3',
            colorInfoBorder: '#225555',
            colorBorder: '#225555',
            colorBorderSecondary: '#225555',
        },
        Modal: {
            boxShadow: 'none',
        },
        Card: {
            colorBgContainer: '#BBAA99',
        },
        Tooltip: {
            borderRadius: 6,
            colorBorder: '#225555',
            algorithm: true,
        },
        Select: {
            optionSelectedBg: '#CBC4AF',
        },
    }
}
export default useCartoonTheme