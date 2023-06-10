import { Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { AlgorithmParams } from 'src/app/model/algorithm-params';
import { TimetableFile } from 'src/app/model/timetable-file';
import { AlgorithmParamsService } from 'src/app/services/algorithm-params.service';
import { TimetableFileService } from 'src/app/services/timetable-file.service';
import { TimetableService } from 'src/app/services/timetable.service';

@Component({
  selector: 'app-file-transfer',
  templateUrl: './file-transfer.component.html',
  styleUrls: ['./file-transfer.component.css']
})
export class FileTransferComponent implements OnInit, OnDestroy {
  public fileUpload: any = null;

  public selectedFile: any = null;
  public selectedFileName: string = '';
  public timetableFileNames: string[] = [];

  public uploadOkDiv: HTMLDivElement | null = null;
  public downloadOkDiv: HTMLDivElement | null = null;

  public badInputDiv: HTMLDivElement | null = null;
  public okInputDiv: HTMLDivElement | null = null;

  public timetableFileOkDiv: HTMLDivElement | null = null;

  public isFileLoading: boolean = false;

  public params: any = {
    numberOfDays: "5",
    startTime: '08:00',
    endTime: '20:00',
    generalDuration: "2",
  }

  public paramsToSend: AlgorithmParams = {
    numberOfDays: 5,
    startTime: '08:00',
    endTime: '20:00',
    generalDuration: 2,
  }

  constructor(
    private timetableFileService: TimetableFileService,
    private timetableService: TimetableService,
    private algorithmParamsService: AlgorithmParamsService,
    private sanitizer: DomSanitizer,
    private ngZone: NgZone) { }
  
  ngOnInit(): void {
    this.updateAllFileNames();

    this.uploadOkDiv = document.getElementById('successful-upload') as HTMLDivElement;
    this.downloadOkDiv = document.getElementById('successful-download') as HTMLDivElement;

    this.badInputDiv = document.getElementById('bad-params-input') as HTMLDivElement;
    this.okInputDiv = document.getElementById('successful-param-update') as HTMLDivElement;

    this.timetableFileOkDiv = document.getElementById('timetable-file-change') as HTMLDivElement;

    this.algorithmParamsService.getAlgorithmParams()
    .subscribe((params: AlgorithmParams) => {
      this.paramsToSend = params;
      this.params.numberOfDays = params.numberOfDays.toString();
      this.params.startTime = params.startTime;
      this.params.endTime = params.endTime;
      this.params.generalDuration = params.generalDuration.toString();
    });
  }

  ngOnDestroy(): void {
  }

  private isValidInput(): boolean {
    // check validity
    let numberOfDays: number = parseInt(this.params.numberOfDays);
    if (Number.isNaN(numberOfDays)|| numberOfDays < 0 || numberOfDays > 7) {
      return false;
    }

    let startTime: string = this.params.startTime;
    let hour: string = startTime.split(':')[0];
    if (hour === startTime) {
      return false;
    }
    let starthourNumber: number = parseInt(hour);
    if (Number.isNaN(starthourNumber) || starthourNumber < 7 || starthourNumber >= 24) {
      return false;
    }

    let minuteNumber = parseInt(startTime.split(':')[1]);
    if (Number.isNaN(minuteNumber) || minuteNumber < 0 || minuteNumber >= 60) {
      return false;
    }
  
    let endTime: string = this.params.endTime;
    hour = endTime.split(':')[0];
    if (hour === endTime) {
      return false;
    }
    let endhourNumber = parseInt(hour);
    if (Number.isNaN(starthourNumber) || starthourNumber < starthourNumber || starthourNumber >= 24) {
      return false;
    }

    minuteNumber = parseInt(endTime.split(':')[1]);
    if (Number.isNaN(minuteNumber) || minuteNumber < 0 || minuteNumber >= 60) {
      return false;
    }

    let generalDuration: number = parseInt(this.params.generalDuration);
    if (Number.isNaN(generalDuration) || generalDuration < 1 || generalDuration > 24) {
      return false;
    }

    return true;
  }

  public updateParams(): void {
    if (!this.isValidInput()) {
      if (this.badInputDiv == null) {
        return;
      }
      this.badInputDiv.style.display = 'block';
      return;
    } else {
      if (this.badInputDiv == null) {
        return;
      }
      this.badInputDiv.style.display = 'none';
    }

    this.paramsToSend.numberOfDays = parseInt(this.params.numberOfDays);
    this.paramsToSend.startTime = this.params.startTime;
    this.paramsToSend.endTime = this.params.endTime;
    this.paramsToSend.generalDuration = parseInt(this.params.generalDuration);

    this.algorithmParamsService.setAlgorithmParams(this.paramsToSend)
    .subscribe((params: AlgorithmParams) => {
        this.ngZone.run(() => {
          if (this.okInputDiv != null) {
          
            this.okInputDiv.style.display = 'block';
            setTimeout(() => {
              if (this.okInputDiv == null) {
                return;
              }
              this.okInputDiv.style.display = 'none';
            }, 5000);
          }
        });
    });
  }

  private updateAllFileNames(): void {
    this.timetableFileService.getAllFileNames()
    .subscribe((timetableFileNames: string[]) => {
      this.timetableFileNames = timetableFileNames;
      console.log(this.timetableFileNames);
    });
  }

  public onSelectUploadFile(event: any): void {
    this.fileUpload = event.target.files[0] ?? null;
    
    console.log(this.fileUpload);
    if (this.fileUpload == null) {
      return;
    }

    let file: File = this.fileUpload;
    let fileName: string = file.name;

    const formData: FormData = new FormData();
    formData.append('file', file, fileName);

    console.log(formData);

    this.timetableFileService.uploadFile(formData)
    .subscribe((response: any) => {
      this.ngZone.run(() => {
        if (this.uploadOkDiv == null) {
          return;
        }

        this.uploadOkDiv.style.display = 'block';
        setTimeout(() => {
          if (this.uploadOkDiv == null) {
            return;
          }

          this.uploadOkDiv.style.display = 'none';
        }, 5000);
      });

      this.updateAllFileNames();
    });
  }

  public deleteFile(selectedFileName: string): void {
    this.timetableFileService.getTimetableFileIdByName(selectedFileName)
    .subscribe((id: number) => {
      let timetableFileId: number = id;
      this.timetableFileService.deleteFileById(timetableFileId)
      .subscribe(() => {
        console.log(`File with id ${timetableFileId} has been deleted.`);

        this.updateAllFileNames();
      });
    });
  }

  public useSelectedFile(): void {
    if (this.selectedFileName == '') {
      return;
    }

    this.isFileLoading = true;

    this.timetableFileService.setFileAsDefault(this.selectedFileName)
    .subscribe((timetableFile: TimetableFile) => {
      console.log(`File ${timetableFile.name} has been set as default.`);
      window.location.reload();

      this.ngZone.run(() => {
        if (this.timetableFileOkDiv == null) {
          return;
        }

        this.timetableFileOkDiv.style.display = 'block';
        setTimeout(() => {
          if (this.timetableFileOkDiv == null) {
            return;
          }

          this.timetableFileOkDiv.style.display = 'none';

          this.isFileLoading = false;
        }, 5000);
      });
    });
  }

  public downloadCurrentConfiguration(): void {
    this.timetableService.downloadCurrentConfiguration()
    .subscribe((response: any) => {
      const blob = new Blob([response], { type: 'text/xml' });
      const url = window.URL.createObjectURL(blob);
      
      const link = document.createElement('a');
      link.href = url;
      link.download = 'timetable-export.xml';
      link.click();

      window.URL.revokeObjectURL(url);
      this.ngZone.run(() => {
        if (this.downloadOkDiv == null) {
          return;
        }
        
        this.downloadOkDiv.style.display = 'block';
        setTimeout(() => {
          if (this.downloadOkDiv == null) {
            return;
          }

          this.downloadOkDiv.style.display = 'none';
        }, 5000);
      });
    });
  }

  public onHover(): void {

  }


}
