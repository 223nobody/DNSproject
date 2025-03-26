import { router } from '@kit.ArkUI';
import axios, { AxiosError, AxiosResponse } from '@ohos/axios';



interface Result{
  code?:number,
  msg?:string,
  data:DNS[],
}
interface DNS{
  id?:number,
  url?:string,
  dnscode?:string,
}

@Entry
@Component
struct HistoryPage {
  @State dnses: DNS[] = []

  aboutToAppear(): void {
    axios.get<Result, AxiosResponse<Result>, Result>(
      'http://localhost:8080/dns',
    ).then((data: AxiosResponse<Result>) => {
      this.dnses = data.data.data
    }).catch((error: AxiosError) => {
      console.error(error.message);
    })
  }

  build() {
    Row() {
      Column() {
        Row() {
          Image($r("app.media.back")).width(30)
            .onClick(() => {
              router.pushUrl({
                url: 'pages/HomePage',
              })
            })
          Text("查询历史").fontSize(30).fontWeight(600).position({ x: 105 })
        }.width("95%")
        .margin(10)

        Scroll() {
          List({ space: 15 }) {
            ForEach(this.dnses, (item: DNS) => {
              ListItem() {
                Row() {
                  Text(item.url)
                    .fontSize(20)
                    .fontWeight(600)
                    .maxLines(2)                      // 最多显示两行
                    .textOverflow({ overflow: TextOverflow.Ellipsis })
                    .lineHeight(20)                   // 确保行高固定
                    .height(40)                       // 固定总高度（2行）
                    .layoutWeight(1)                  // 占据剩余空间
                    .flexShrink(1)                    // 允许内容收缩

                  Text(item.dnscode)
                    .fontSize(20)
                    .fontColor("#00BFFF")
                    .flexShrink(0)                    // 禁止被压缩
                }
                .width("90%")
                .justifyContent(FlexAlign.SpaceBetween)
                .margin({ left: 10, right: 8 })
                .borderRadius(10)
              }
            })
          }.width("100%")
          .height("150%")
        }
      }
    }
  }
}


