import { router } from '@kit.ArkUI';
import axios, { AxiosError, AxiosResponse } from '@ohos/axios';

interface Result {
  code?: number,
  msg?: string,
  data: DNS,
}

interface DNS {
  id?: number,
  url?: string,
  dnscode?: string,
}

@Entry
@Component
struct HomePage {
  @State url: string = ''
  @State dnscode: string = ''
  @State dnses: DNS[] = []
  @State flag: number = 0;

  build() {
    Column() {
      Row() {
        Text("域名解析查询")
          .fontSize(30)
          .fontWeight(FontWeight.Bold)
          .fontColor("#1677FF")
      }
      .width('100%')
      .justifyContent(FlexAlign.Center)
      .margin({ top: 20, bottom: 30 })
      Column() {
        Row() {
          Text("请输入域名")
            .fontSize(15)
            .fontColor("#666666")
            .margin({ right: 10 })
          TextInput()
            .width('70%')
            .height(40)
            .fontSize(18)
            .placeholderFont({ size: 16 })
            .borderRadius(8)
            .backgroundColor("#FFFFFF")
            .padding(8)
            .onChange((value) => {
              this.url = value;
            })
        }
        .alignItems(VerticalAlign.Center)
        .padding(10)
        Button("立即查询")
          .width('60%')
          .height(45)
          .backgroundColor("#1677FF")
          .fontColor("#FFFFFF")
          .fontSize(20)
          .margin({ top: 15 })
          .onClick(() => {
            this.handleQuery();
          })
      }
      .width('90%')
      .backgroundColor("#F5F5F5")
      .borderRadius(15)
      .padding(15)
      .shadow({ radius: 8, color: "#20000000", offsetX: 2, offsetY: 4 })
      Column() {
        if (this.dnscode) {
          Text("解析结果：")
            .fontSize(18)
            .fontColor("#666666")
            .margin({ bottom: 8 })
          Text(this.dnscode)
            .fontSize(20)
            .fontColor(this.flag === 0 ? "#FF4500" : "#00BFFF")
            .padding(10)
            .backgroundColor("#FFFFFF")
            .borderRadius(10)
            .width('80%')
            .textAlign(TextAlign.Center)
            .maxLines(1)
            .textOverflow({ overflow: TextOverflow.Ellipsis })
        }
      }
      .margin({ top: 30 })
      Button("查看历史记录")
        .width('50%')
        .height(45)
        .backgroundColor("#20B2AA")
        .fontColor("#FFFFFF")
        .fontSize(18)
        .margin({ top: 40 })
        .onClick(() => {
          router.pushUrl({
            url: 'pages/HistoryPage',
          })
        })
    }
    .width('100%')
    .height('100%')
    .backgroundColor("#F0F8FF")
    .padding(20)
    .justifyContent(FlexAlign.Start)
  }
  private handleQuery() {
    axios.post<Result, AxiosResponse<Result>, DNS>(
      'http://localhost:8080/dns',
      {},
      {params:{url:this.url}}
    ).then((data: AxiosResponse<Result>) => {
    }).catch((error: AxiosError) => {
      console.error(error.message);
    })
    axios.get<Result, AxiosResponse<Result>, DNS>(
      'http://localhost:8080/dns1',
      { params: { url : this.url } }
    ).then((data: AxiosResponse<Result>) => {
      if(data.data.data.dnscode!=undefined)
      {
        this.dnscode = data.data.data.dnscode;
      }
    }).catch((error: AxiosError) => {
      console.error(error.message);
    })
  }
}