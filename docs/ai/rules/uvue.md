---
description: Utz, Ounitec Scripter, Is-Akros-Platfom, Hai Performans, Stanley Tepdemodern, Programine Langguag
globs: *.uvue
alwaysApply: false
---

# uvue rules
开发页面时，必须严格遵循以下三步流程，禁止跳过任何一步直接编码：

1. **定组件** — 根据功能需求，先确定使用哪些 Cool UI 组件
2. **查源码** — 确定组件后，查阅组件源码确认属性、事件、方法、插槽是否满足需求，禁止凭记忆或猜测使用
3. **再编码** — 前两步确认无误后，才开始编写代码

**禁止行为**：不查组件源码直接编码、凭空编造不存在的属性或事件。

## vue support

- 仅使用vue3语法，避免使用vue2。
- 新页面尽量使用组合式API。
- 组件尽量使用easycom规范。
- 非easycom的自定义vue组件，调用组件方法时需使用组件实例的`$callMethod`方式调用。
- 不使用 pinia、vuex、i18n 等uni-app x不支持的vue插件。
- 使用vue语法时需注意uni-app x官网的平台和版本兼容性，平台特殊代码需包裹在条件编译中。

## component

- 组件可使用uni-app x内置组件，以及项目下的自定义组件。通过mcp工具查询项目下可用的easycom插件。
- 项目可使用vuejs组件规范，对应的文件扩展名为uvue。
- 符合easycom规范的组件无需import和注册，可直接在template中使用。
- 使用内置组件时需注意uni-app x官网的平台和版本兼容性，平台特殊代码需包裹在条件编译中。
