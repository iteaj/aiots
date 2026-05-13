// ========== illustrationTheme.ts ==========

import { theme } from 'antdv-next'

const themeConfig = {
    token: {
        colorText: '#2C2C2C',
        colorPrimary: '#52C41A',
        colorSuccess: '#51CF66',
        colorWarning: '#FFD93D',
        colorError: '#FA5252',
        colorInfo: '#4DABF7',
        colorBorder: '#2C2C2C',
        colorBorderSecondary: '#2C2C2C',
        lineWidth: 3,
        lineWidthBold: 3,
        borderRadius: 12,
        borderRadiusLG: 16,
        borderRadiusSM: 8,
        controlHeight: 40,
        controlHeightSM: 34,
        controlHeightLG: 48,
        fontSize: 15,
        fontWeightStrong: 600,
        colorBgBase: '#FFF9F0',
        colorBgContainer: '#FFFFFF',
    },
    components: {
        Button: {
            primaryShadow: 'none',
            dangerShadow: 'none',
            defaultShadow: 'none',
            fontWeight: 600,
        },
        Modal: {
            boxShadow: 'none',
        },
        Card: {
            boxShadow: '4px 4px 0 #2C2C2C',
            colorBgContainer: '#FFF0F6',
        },
        Tooltip: {
            colorBorder: '#2C2C2C',
            colorBgSpotlight: 'rgba(100, 100, 100, 0.95)',
            borderRadius: 8,
        },
        Select: {
            optionSelectedBg: 'transparent',
        },
        Slider: {
            dotBorderColor: '#237804',
            dotActiveBorderColor: '#237804',
            colorPrimaryBorder: '#237804',
            colorPrimaryBorderHover: '#237804',
        },
    }
}

function useIllustrationTheme(useStyles) {
    const { styles } = useStyles

    return {
        theme: {
            token: {
                colorText: '#2C2C2C',
                colorPrimary: '#52C41A',
                colorSuccess: '#51CF66',
                colorWarning: '#FFD93D',
                colorError: '#FA5252',
                colorInfo: '#4DABF7',
                colorBorder: '#2C2C2C',
                colorBorderSecondary: '#2C2C2C',
                lineWidth: 3,
                lineWidthBold: 3,
                borderRadius: 12,
                borderRadiusLG: 16,
                borderRadiusSM: 8,
                controlHeight: 40,
                controlHeightSM: 34,
                controlHeightLG: 48,
                fontSize: 15,
                fontWeightStrong: 600,
                colorBgBase: '#FFF9F0',
                colorBgContainer: '#FFFFFF',
            },
            components: {
                Button: {
                    primaryShadow: 'none',
                    dangerShadow: 'none',
                    defaultShadow: 'none',
                    fontWeight: 600,
                },
                Modal: {
                    boxShadow: 'none',
                },
                Card: {
                    boxShadow: '4px 4px 0 #2C2C2C',
                    colorBgContainer: '#FFF0F6',
                },
                Tooltip: {
                    colorBorder: '#2C2C2C',
                    colorBgSpotlight: 'rgba(100, 100, 100, 0.95)',
                    borderRadius: 8,
                },
                Select: {
                    optionSelectedBg: 'transparent',
                },
                Slider: {
                    dotBorderColor: '#237804',
                    dotActiveBorderColor: '#237804',
                    colorPrimaryBorder: '#237804',
                    colorPrimaryBorderHover: '#237804',
                },
            },
        },
        button: {
            classes: {
                root: styles.buttonRoot,
            },
        },
        modal: {
            classes: {
                container: styles.modalContainer,
            },
        },
        alert: {
            classes: {
                root: styles.illustrationBorder,
            },
        },
        colorPicker: {
            arrow: false,
            classes: {
                root: styles.illustrationBox,
            },
        },
        popover: {
            arrow: false,
            classes: {
                container: styles.illustrationBox,
            },
        },
        tooltip: {
            arrow: false,
            classes: {
                root: styles.tooltipRoot,
                container: styles.illustrationBox,
            },
        },
        dropdown: {
            classes: {
                root: styles.popupBox,
            },
        },
        select: {
            classes: {
                root: styles.illustrationBox,
                popup: {
                    root: styles.popupBox,
                },
            },
        },
        input: {
            classes: {
                root: styles.illustrationBox,
            },
        },
        inputNumber: {
            classes: {
                root: styles.illustrationBox,
                actions: styles.inputNumberActions,
            },
        },
        progress: {
            classes: {
                rail: styles.progressRail,
                track: styles.progressTrack,
            },
            styles: {
                rail: {
                    height: '16px',
                },
                track: {
                    height: '10px',
                },
            },
        },
        token: {
            colorText: '#2C2C2C',
            colorPrimary: '#52C41A',
            colorSuccess: '#51CF66',
            colorWarning: '#FFD93D',
            colorError: '#FA5252',
            colorInfo: '#4DABF7',
            colorBorder: '#2C2C2C',
            colorBorderSecondary: '#2C2C2C',
            lineWidth: 3,
            lineWidthBold: 3,
            borderRadius: 12,
            borderRadiusLG: 16,
            borderRadiusSM: 8,
            controlHeight: 40,
            controlHeightSM: 34,
            controlHeightLG: 48,
            fontSize: 15,
            fontWeightStrong: 600,
            colorBgBase: '#FFF9F0',
            colorBgContainer: '#FFFFFF',
        },
        components: {
            Button: {
                primaryShadow: 'none',
                dangerShadow: 'none',
                defaultShadow: 'none',
                fontWeight: 600,
            },
            Modal: {
                boxShadow: 'none',
            },
            Card: {
                boxShadow: '4px 4px 0 #2C2C2C',
                colorBgContainer: '#FFF0F6',
            },
            Tooltip: {
                colorBorder: '#2C2C2C',
                colorBgSpotlight: 'rgba(100, 100, 100, 0.95)',
                borderRadius: 8,
            },
            Select: {
                optionSelectedBg: 'transparent',
            },
            Slider: {
                dotBorderColor: '#237804',
                dotActiveBorderColor: '#237804',
                colorPrimaryBorder: '#237804',
                colorPrimaryBorderHover: '#237804',
            },
        }
    }

    // return {...IllustrationTheme, theme: themeConfig}
}

export default useIllustrationTheme

